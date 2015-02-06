/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2014 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.module.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.lang.NotImplementedException;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.query.GQL;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.core.query.lucene.QueryResultImpl;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.QueryResult;
import com.openkm.bean.Repository;
import com.openkm.bean.ResultSet;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.DashboardDAO;
import com.openkm.dao.QueryParamsDAO;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.SearchModule;
import com.openkm.module.jcr.base.BaseDocumentModule;
import com.openkm.module.jcr.base.BaseFolderModule;
import com.openkm.module.jcr.base.BaseMailModule;
import com.openkm.module.jcr.stuff.JCRUtils;
import com.openkm.module.jcr.stuff.JcrSessionManager;
import com.openkm.util.FormUtils;
import com.openkm.util.UserActivity;

public class JcrSearchModule implements SearchModule {
	private static Logger log = LoggerFactory.getLogger(JcrSearchModule.class);
	
	@Override
	public List<QueryResult> findByContent(String token, String words) throws IOException, ParseException, RepositoryException,
			DatabaseException {
		log.debug("findByContent({}, {})", token, words);
		QueryParams params = new QueryParams();
		params.setContent(words);
		List<QueryResult> ret = find(token, params);
		log.debug("findByContent: {}", ret);
		return ret;
	}
	
	@Override
	public List<QueryResult> findByName(String token, String words) throws IOException, ParseException, RepositoryException,
			DatabaseException {
		log.debug("findByName({}, {})", token, words);
		QueryParams params = new QueryParams();
		params.setName(words);
		List<QueryResult> ret = find(token, params);
		log.debug("findByName: {}", ret);
		return ret;
	}
	
	@Override
	public List<QueryResult> findByKeywords(String token, Set<String> words) throws IOException, ParseException, RepositoryException,
			DatabaseException {
		log.debug("findByKeywords({}, {})", token, words);
		QueryParams params = new QueryParams();
		params.setKeywords(words);
		List<QueryResult> ret = find(token, params);
		log.debug("findByKeywords: {}", ret);
		return ret;
	}
	
	@Override
	public List<QueryResult> find(String token, QueryParams params) throws IOException, ParseException, RepositoryException,
			DatabaseException {
		log.debug("find({}, {})", token, params);
		List<QueryResult> ret = findPaginated(token, params, 0, Config.MAX_SEARCH_RESULTS).getResults();
		log.debug("find: {}", ret);
		return ret;
	}
	
	@Override
	public ResultSet findPaginated(String token, QueryParams params, int offset, int limit) throws IOException, ParseException,
			RepositoryException, DatabaseException {
		log.debug("findPaginated({}, {}, {}, {})", new Object[] { token, params, offset, limit });
		String type = null;
		String query = null;
		
		if (!"".equals(params.getStatementQuery())
				&& (Query.XPATH.equals(params.getStatementType()) || Query.SQL.equals(params.getStatementType()))) {
			query = params.getStatementQuery();
			type = params.getStatementType();
		} else {
			query = prepareStatement(params);
			type = Query.XPATH;
		}
		
		ResultSet rs = findByStatementPaginated(token, query, type, offset, limit);
		log.debug("findPaginated: {}", rs);
		return rs;
	}
	
	/**
	 * Escape jcr:contains searchExp (view 6.6.5.2) Text.escapeIllegalXpathSearchChars(searchTerm).replaceAll("'", "''")
	 * 
	 * @see http
	 *      ://svn.apache.org/repos/asf/jackrabbit/branches/2.2/jackrabbit-jcr-commons/src/main/java/org/apache/
	 *      jackrabbit
	 *      /util/Text.java
	 * @see http://wiki.apache.org/jackrabbit/EncodingAndEscaping
	 * @param str The String to be escaped.
	 * @return The escaped String.
	 */
	private String escapeContains(String str) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			if (c == '!' || c == '(' || c == ':' || c == '^' || c == '"' || c == '[' || c == ']' || c == '{' || c == '}' || c == '?') {
				sb.append('\\');
			}
			
			sb.append(c);
		}
		
		return sb.toString().replace("'", "''");
	}
	
	/**
	 * Escape XPath string
	 * 
	 * @see org.apache.jackrabbit.util.Text.escapeIllegalXpathSearchChars(String s)
	 * @param str The String to be escaped.
	 * @return The escaped String.
	 */
	private String escapeXPath(String str) {
		String ret = str.replace("'", "''");
		return ret;
	}
	
	/**
	 * Prepare statement
	 */
	public String prepareStatement(QueryParams params) throws IOException, ParseException {
		log.debug("prepareStatement({})", params);
		StringBuilder sb = new StringBuilder();
		
		// Clean params
		params.setName(params.getName() != null ? params.getName().trim() : "");
		params.setContent(params.getContent() != null ? params.getContent().trim() : "");
		params.setKeywords(params.getKeywords() != null ? params.getKeywords() : new HashSet<String>());
		params.setCategories(params.getCategories() != null ? params.getCategories() : new HashSet<String>());
		params.setMimeType(params.getMimeType() != null ? params.getMimeType().trim() : "");
		params.setAuthor(params.getAuthor() != null ? params.getAuthor().trim() : "");
		params.setPath(params.getPath() != null ? params.getPath().trim() : "");
		params.setMailSubject(params.getMailSubject() != null ? params.getMailSubject().trim() : "");
		params.setMailFrom(params.getMailFrom() != null ? params.getMailFrom().trim() : "");
		params.setMailTo(params.getMailTo() != null ? params.getMailTo().trim() : "");
		params.setProperties(params.getProperties() != null ? params.getProperties() : new HashMap<String, String>());
		
		// Domains
		boolean document = (params.getDomain() & QueryParams.DOCUMENT) != 0;
		boolean folder = (params.getDomain() & QueryParams.FOLDER) != 0;
		boolean mail = (params.getDomain() & QueryParams.MAIL) != 0;
		log.debug("doc={}, fld={}, mail={}", new Object[] { document, folder, mail });
		
		// Escape
		if (!params.getName().equals("")) {
			params.setName(escapeContains(params.getName()));
		}
		
		if (!params.getContent().equals("")) {
			params.setContent(escapeContains(params.getContent()));
		}
		
		if (!params.getContent().isEmpty() || !params.getName().isEmpty() || !params.getKeywords().isEmpty()
				|| !params.getMimeType().isEmpty() || !params.getAuthor().isEmpty() || !params.getProperties().isEmpty()
				|| !params.getMailSubject().isEmpty() || !params.getMailFrom().isEmpty() || !params.getMailTo().isEmpty()
				|| (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null)) {
			
			// Construct the query
			sb.append("/jcr:root" + ISO9075.encodePath(params.getPath()) + "//*[@jcr:primaryType eq 'okm:void'");
			
			/**
			 * DOCUMENT
			 */
			if (document) {
				sb.append(" or (@jcr:primaryType eq 'okm:document'");
				
				if (!params.getContent().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(okm:content,'" + params.getContent() + "')");
				}
				
				if (!params.getName().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(@okm:name,'" + params.getName() + "')");
				}
				
				if (!params.getKeywords().isEmpty()) {
					for (Iterator<String> it = params.getKeywords().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:keywords='" + escapeContains(it.next()) + "'");
					}
				}
				
				if (!params.getCategories().isEmpty()) {
					for (Iterator<String> it = params.getCategories().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:categories='" + it.next() + "'");
					}
				}
				
				if (!params.getMimeType().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("@okm:content/jcr:mimeType='" + params.getMimeType() + "'");
				}
				
				if (!params.getAuthor().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("@okm:content/okm:author='" + params.getAuthor() + "'");
				}
				
				if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("(");
					sb.append("@okm:content/jcr:lastModified >= xs:dateTime('" + ISO8601.format(params.getLastModifiedFrom()) + "')");
					sb.append(" and ");
					sb.append("@okm:content/jcr:lastModified <= xs:dateTime('" + ISO8601.format(params.getLastModifiedTo()) + "')");
					sb.append(")");
				}
				
				sb.append(preparePropertyGroups(params));
				sb.append(")");
			}
			
			/**
			 * FOLDER
			 */
			if (folder) {
				sb.append(" or (@jcr:primaryType eq 'okm:folder'");
				
				if (!params.getName().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(@okm:name,'" + params.getName() + "')");
				}
				
				if (!params.getKeywords().isEmpty()) {
					for (Iterator<String> it = params.getKeywords().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:keywords='" + escapeContains(it.next()) + "'");
					}
				}
				
				if (!params.getCategories().isEmpty()) {
					for (Iterator<String> it = params.getCategories().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:categories='" + it.next() + "'");
					}
				}
				
				if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("(");
					sb.append("@jcr:created >= xs:dateTime('" + ISO8601.format(params.getLastModifiedFrom()) + "')");
					sb.append(" and ");
					sb.append("@jcr:created <= xs:dateTime('" + ISO8601.format(params.getLastModifiedTo()) + "')");
					sb.append(")");
				}
				
				sb.append(preparePropertyGroups(params));
				sb.append(")");
			}
			
			/**
			 * MAIL
			 */
			if (mail) {
				sb.append(" or (@jcr:primaryType eq 'okm:mail'");
				
				if (!params.getContent().equals("")) {
					sb.append(" and jcr:contains(.,'" + params.getContent() + "')");
				}
				
				if (!params.getMailSubject().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(@okm:subject,'" + params.getMailSubject() + "')");
				}
				
				if (!params.getMailFrom().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(@okm:from,'" + params.getMailFrom() + "')");
				}
				
				if (!params.getMailTo().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("jcr:contains(@okm:to,'" + params.getMailTo() + "')");
				}
				
				if (!params.getMimeType().equals("")) {
					sb.append(" " + params.getOperator() + " ");
					sb.append("@okm:content/jcr:mimeType='" + params.getMimeType() + "'");
				}
				
				if (!params.getKeywords().isEmpty()) {
					for (Iterator<String> it = params.getKeywords().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:keywords='" + escapeContains(it.next()) + "'");
					}
				}
				
				if (!params.getCategories().isEmpty()) {
					for (Iterator<String> it = params.getCategories().iterator(); it.hasNext();) {
						sb.append(" " + params.getOperator() + " ");
						sb.append("@okm:categories='" + it.next() + "'");
					}
				}
				
				sb.append(preparePropertyGroups(params));
				sb.append(")");
			}
			
			sb.append("] order by @jcr:score descending");
		}
		
		log.debug("prepareStatement: {}", sb.toString());
		return sb.toString();
	}
	
	/**
	 * Create XPath related to property groups.
	 */
	private Object preparePropertyGroups(QueryParams params) throws IOException, ParseException {
		StringBuilder sb = new StringBuilder();
		
		if (!params.getProperties().isEmpty()) {
			Map<PropertyGroup, List<FormElement>> formsElements = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			
			for (Iterator<Entry<String, String>> it = params.getProperties().entrySet().iterator(); it.hasNext();) {
				Entry<String, String> ent = it.next();
				FormElement fe = FormUtils.getFormElement(formsElements, ent.getKey());
				
				if (fe != null && ent.getValue() != null) {
					String valueTrimmed = ent.getValue().trim();
					
					if (!valueTrimmed.equals("")) {
						sb.append(" " + params.getOperator() + " ");
						
						if (fe instanceof Select) {
							sb.append("@" + ent.getKey() + "='" + escapeXPath(valueTrimmed) + "'");
						} else if (fe instanceof Input && ((Input) fe).getType().equals(Input.TYPE_DATE)) {
							String[] date = valueTrimmed.split(",");
							
							if (date.length == 2) {
								sb.append("@" + ent.getKey() + " >= '" + date[0] + "'");
								sb.append(" and ");
								sb.append("@" + ent.getKey() + " <= '" + date[1] + "'");
							}
						} else {
							sb.append("jcr:contains(@" + ent.getKey() + ",'" + escapeContains(valueTrimmed) + "')");
						}
					}
				}
			}
		}
		
		return sb.toString();
	}
	
	public List<QueryResult> findByStatement(String token, String statement, String type) throws RepositoryException, DatabaseException {
		log.debug("findByStatement({}, {})", token, statement);
		List<QueryResult> ret = findByStatementPaginated(token, statement, type, 0, Config.MAX_SEARCH_RESULTS).getResults();
		log.debug("findByStatement: {}", ret);
		return ret;
	}
	
	public ResultSet findByStatementPaginated(String token, String statement, String type, int offset, int limit)
			throws RepositoryException, DatabaseException {
		log.debug("findByStatement({}, {}, {}, {}, {})", new Object[] { token, statement, type, offset, limit });
		ResultSet rs = new ResultSet();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			if (statement != null && !statement.equals("")) {
				Workspace workspace = session.getWorkspace();
				QueryManager queryManager = workspace.getQueryManager();
				Query query = queryManager.createQuery(statement, type);
				rs = executeQuery(session, query, offset, limit);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_BY_STATEMENT", null, null, type + ", " + statement);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("findByStatement: {}", rs);
		return rs;
	}
	
	/**
	 * Execute query
	 */
	private ResultSet executeQuery(Session session, Query query, int offset, int limit) throws RepositoryException {
		log.debug("executeQuery({}, {}, {}, {})", new Object[] { session, query, offset, limit });
		ResultSet rs = new ResultSet();
		
		try {
			ArrayList<QueryResult> al = new ArrayList<QueryResult>();
			
			// http://n4.nabble.com/Query-performance-for-large-query-results-td531360.html
			((QueryImpl) query).setLimit(limit);
			((QueryImpl) query).setOffset(offset);
			QueryResultImpl result = (QueryResultImpl) query.execute();
			RowIterator rit = result.getRows();
			rs.setTotal(result.getTotalSize());
			
			while (rit.hasNext()) {
				Row row = rit.nextRow();
				QueryResult qr = queryRowResultDigester(session, row);
				al.add(qr);
			}
			
			rs.setResults(al);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		}
		
		log.debug("executeQuery: {}", rs);
		return rs;
	}
	
	/**
	 * Execute simple query
	 */
	private ResultSet executeSimpleQuery(Session session, String statement, int offset, int limit) throws RepositoryException {
		log.debug("executeSimpleQuery({}, {}, {}, {})", new Object[] { session, statement, offset, limit });
		ResultSet rs = new ResultSet();
		
		if (statement != null && !statement.equals("")) {
			if (!statement.contains("path:")) {
				statement = "path:\"/" + Repository.ROOT + "\" " + statement;
			}
			
			if (!statement.contains("limit:") && limit < Config.MAX_SEARCH_RESULTS) {
				StringBuilder sb = new StringBuilder();
				sb.append("limit:").append(offset).append("..").append(offset + limit);
				statement = statement.concat(" ").concat(sb.toString());
			}
			
			try {
				ArrayList<QueryResult> al = new ArrayList<QueryResult>();
				log.info("Statement: {}", statement);
				RowIterator rit = GQL.execute(statement, session);
				rs.setTotal(rit.getSize());
				
				while (rit.hasNext()) {
					Row row = rit.nextRow();
					QueryResult qr = queryRowResultDigester(session, row);
					al.add(qr);
				}
				
				rs.setResults(al);
			} catch (javax.jcr.RepositoryException e) {
				log.error(e.getMessage(), e);
				throw new RepositoryException(e.getMessage(), e);
			}
		}
		
		log.debug("executeSimpleQuery: {}", rs);
		return rs;
	}
	
	/**
	 * Convert Row to QueryResult
	 */
	private QueryResult queryRowResultDigester(Session session, Row row) throws javax.jcr.PathNotFoundException,
			javax.jcr.RepositoryException {
		String path = row.getValue(JcrConstants.JCR_PATH).getString();
		log.debug("queryRowResultDigester: {}", path);
		Node node = session.getRootNode().getNode(path.substring(1));
		QueryResult qr = new QueryResult();
		
		if (node.isNodeType(Document.CONTENT_TYPE)) {
			Document doc = BaseDocumentModule.getProperties(session, node.getParent());
			qr.setDocument(doc);
		} else if (node.isNodeType(Document.TYPE)) {
			Document doc = BaseDocumentModule.getProperties(session, node);
			
			try {
				if (node.getParent().isNodeType(Mail.TYPE)) {
					qr.setAttachment(doc);
				} else {
					qr.setDocument(doc);
				}
			} catch (javax.jcr.AccessDeniedException e) {
				qr.setDocument(doc);
			}
		} else if (node.isNodeType(Folder.TYPE)) {
			Folder fld = BaseFolderModule.getProperties(session, node);
			qr.setFolder(fld);
		} else if (node.isNodeType(Mail.TYPE)) {
			Mail mail = BaseMailModule.getProperties(session, node);
			qr.setMail(mail);
		}
		
		qr.setScore(row.getValue(JcrConstants.JCR_SCORE).getLong());
		Value excerpt = row.getValue("rep:excerpt(okm:content)");
		
		if (excerpt != null) {
			qr.setExcerpt(excerpt.getString());
		}
		
		return qr;
	}
	
	@Override
	public long saveSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("saveSearch({}, {})", token, params);
		Session session = null;
		long id = 0;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			// TODO This JCR Session could be removed
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			params.setUser(session.getUserID());
			id = QueryParamsDAO.create(params);
			
			// Activity log
			UserActivity.log(session.getUserID(), "SAVE_SEARCH", params.getName(), null, params.toString());
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("saveSearch: {}", id);
		return id;
	}
	
	@Override
	public void updateSearch(String token, QueryParams params) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("updateSearch({}, {})", token, params);
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			// TODO This JCR Session could be removed
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			params.setUser(session.getUserID());
			QueryParamsDAO.update(params);
			
			// Activity log
			UserActivity.log(session.getUserID(), "UPDATE_SEARCH", params.getName(), null, params.toString());
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("updateSearch: void");
	}
	
	@Override
	public QueryParams getSearch(String token, int qpId) throws PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getSearch({}, {})", token, qpId);
		QueryParams qp = new QueryParams();
		Session session = null;
		
		try {
			// TODO This JCR Session could be removed
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			qp = QueryParamsDAO.findByPk(qpId);
			
			// If this is a dashboard user search, dates are used internally
			if (qp.isDashboard()) {
				qp.setLastModifiedFrom(null);
				qp.setLastModifiedTo(null);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_SAVED_SEARCH", Integer.toString(qpId), null, qp.toString());
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getSearch: {}", qp);
		return qp;
	}
	
	@Override
	public List<QueryParams> getAllSearchs(String token) throws RepositoryException, DatabaseException {
		log.debug("getAllSearchs({})", token);
		List<QueryParams> ret = new ArrayList<QueryParams>();
		Session session = null;
		
		try {
			// TODO This JCR Session could be removed
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			List<QueryParams> qParams = QueryParamsDAO.findByUser(session.getUserID());
			
			for (Iterator<QueryParams> it = qParams.iterator(); it.hasNext();) {
				QueryParams qp = it.next();
				
				if (!qp.isDashboard()) {
					ret.add(qp);
				}
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_ALL_SEARCHS", null, null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getAllSearchs: {}", ret);
		return ret;
	}
	
	@Override
	public void deleteSearch(String token, long qpId) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("deleteSearch({}, {})", token, qpId);
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			// TODO This JCR Session could be removed
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			QueryParams qp = QueryParamsDAO.findByPk(qpId);
			QueryParamsDAO.delete(qpId);
			
			// Purge visited nodes table
			if (qp.isDashboard()) {
				DashboardDAO.deleteVisitedNodes(session.getUserID(), qp.getName());
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_SAVED_SEARCH", Long.toString(qpId), null, null);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("deleteSearch: void");
	}
	
	@Override
	public Map<String, Integer> getKeywordMap(String token, List<String> filter) throws RepositoryException, DatabaseException {
		log.debug("getKeywordMap({}, {})", token, filter);
		String statement = "/jcr:root//*[@jcr:primaryType eq 'okm:document' or @jcr:primaryType eq 'okm:mail' or @jcr:primaryType eq 'okm:folder']";
		HashMap<String, Integer> cloud = new HashMap<String, Integer>();
		Session session = null;

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}

			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery(statement, Query.XPATH);
			javax.jcr.query.QueryResult qResult = query.execute();

			for (NodeIterator nit = qResult.getNodes(); nit.hasNext();) {
				Node doc = nit.nextNode();
				Value[] keywordsValue = doc.getProperty(com.openkm.bean.Property.KEYWORDS).getValues();
				ArrayList<String> keywordCollection = new ArrayList<String>();

				for (int i = 0; i < keywordsValue.length; i++) {
					keywordCollection.add(keywordsValue[i].getString());
				}

				if (filter != null && keywordCollection.containsAll(filter)) {
					for (Iterator<String> it = keywordCollection.iterator(); it.hasNext();) {
						String keyword = it.next();

						if (!filter.contains(keyword)) {
							Integer occurs = cloud.get(keyword) != null ? cloud.get(keyword) : 0;
							cloud.put(keyword, occurs + 1);
						}
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getKeywordMap: {}", cloud);
		return cloud;
	}
	
	@Override
	public List<Document> getCategorizedDocuments(String token, String categoryId) throws RepositoryException, DatabaseException {
		log.debug("getCategorizedDocuments({}, {})", token, categoryId);
		List<Document> documents = new ArrayList<Document>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node category = session.getNodeByUUID(categoryId);
			
			for (PropertyIterator it = category.getReferences(); it.hasNext();) {
				Property refProp = it.nextProperty();
				
				if (com.openkm.bean.Property.CATEGORIES.equals(refProp.getName())) {
					Node node = refProp.getParent();
					
					if (node.isNodeType(Document.TYPE)) {
						Document doc = BaseDocumentModule.getProperties(session, node);
						documents.add(doc);
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getCategorizedDocuments: {}", documents);
		return documents;
	}
	
	@Override
	public List<Folder> getCategorizedFolders(String token, String categoryId) throws RepositoryException, DatabaseException {
		log.debug("getCategorizedFolders({}, {})", token, categoryId);
		List<Folder> folders = new ArrayList<Folder>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node category = session.getNodeByUUID(categoryId);
			
			for (PropertyIterator it = category.getReferences(); it.hasNext();) {
				Property refProp = it.nextProperty();
				
				if (com.openkm.bean.Property.CATEGORIES.equals(refProp.getName())) {
					Node node = refProp.getParent();
					
					if (node.isNodeType(Folder.TYPE)) {
						Folder fld = BaseFolderModule.getProperties(session, node);
						folders.add(fld);
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getCategorizedFolders: {}", folders);
		return folders;
	}
	
	@Override
	public List<Mail> getCategorizedMails(String token, String categoryId) throws RepositoryException, DatabaseException {
		log.debug("getCategorizedMails({}, {})", token, categoryId);
		List<Mail> mails = new ArrayList<Mail>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node category = session.getNodeByUUID(categoryId);
			
			for (PropertyIterator it = category.getReferences(); it.hasNext();) {
				Property refProp = it.nextProperty();
				
				if (com.openkm.bean.Property.CATEGORIES.equals(refProp.getName())) {
					Node node = refProp.getParent();
					
					if (node.isNodeType(Mail.TYPE)) {
						Mail mail = BaseMailModule.getProperties(session, node);
						mails.add(mail);
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getCategorizedMails: {}", mails);
		return mails;
	}
	
	@Override
	public List<Document> getDocumentsByKeyword(String token, String keyword) throws RepositoryException, DatabaseException {
		throw new NotImplementedException("getDocumentsByKeyword");
	}
	
	@Override
	public List<Folder> getFoldersByKeyword(String token, String keyword) throws RepositoryException, DatabaseException {
		throw new NotImplementedException("getFoldersByKeyword");
	}
	
	@Override
	public List<Mail> getMailsByKeyword(String token, String keyword) throws RepositoryException, DatabaseException {
		throw new NotImplementedException("getMailsByKeyword");
	}
	
	@Override
	public List<Document> getDocumentsByPropertyValue(String token, String group, String property, String value)
			throws RepositoryException, DatabaseException {
		throw new NotImplementedException("getDocumentsByPropertyValue");
	}
	
	@Override
	public List<Folder> getFoldersByPropertyValue(String token, String group, String property, String value) throws RepositoryException,
			DatabaseException {
		throw new NotImplementedException("getFoldersByPropertyValue");
	}
	
	@Override
	public List<Mail> getMailsByPropertyValue(String token, String group, String property, String value) throws RepositoryException,
			DatabaseException {
		throw new NotImplementedException("getMailsByPropertyValue");
	}
	
	@Override
	public List<QueryResult> findSimpleQuery(String token, String statement) throws RepositoryException, DatabaseException {
		log.debug("findSimpleQuery({}, {})", token, statement);
		List<QueryResult> ret = findSimpleQueryPaginated(token, statement, 0, Config.MAX_SEARCH_RESULTS).getResults();
		log.debug("findSimpleQuery: {}", ret);
		return ret;
		
	}
	
	@Override
	public ResultSet findSimpleQueryPaginated(String token, String statement, int offset, int limit) throws RepositoryException,
			DatabaseException {
		log.debug("findSimpleQueryPaginated({}, {}, {}, {})", new Object[] { token, statement, offset, limit });
		ResultSet rs = new ResultSet();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			rs = executeSimpleQuery(session, statement, offset, limit);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("findSimpleQueryPaginated: {}", rs);
		return rs;
	}
	
	@Override
	public ResultSet findMoreLikeThis(String token, String uuid, int maxResults) throws RepositoryException, DatabaseException {
		throw new NotImplementedException("findMoreLikeThis");
	}
}
