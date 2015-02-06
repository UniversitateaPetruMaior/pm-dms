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

package com.openkm.module.db;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.VersionException;
import com.openkm.dao.NodeBaseDAO;
import com.openkm.dao.bean.NodeBase;
import com.openkm.module.PropertyModule;
import com.openkm.module.db.base.BaseNotificationModule;
import com.openkm.spring.PrincipalUtils;
import com.openkm.util.PathUtils;
import com.openkm.util.UserActivity;

public class DbPropertyModule implements PropertyModule {
	private static Logger log = LoggerFactory.getLogger(DbPropertyModule.class);
	
	@Override
	public void addCategory(String token, String nodeId, String catId) throws VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("addCategory({}, {}, {})", new Object[] { token, nodeId, catId });
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;
		String catPath = null;
		String catUuid = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}
			
			if (PathUtils.isPath(catId)) {
				catPath = catId;
				catUuid = NodeBaseDAO.getInstance().getUuidFromPath(catId);
			} else {
				catPath = NodeBaseDAO.getInstance().getPathFromUuid(catId);
				catUuid = catId;
			}
			
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(nodeUuid);
			NodeBaseDAO.getInstance().addCategory(nodeUuid, catUuid);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "ADD_CATEGORY", null);
			
			// Activity log
			UserActivity.log(auth.getName(), "ADD_CATEGORY", nodeUuid, nodePath, catPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("addCategory: void");
	}
	
	@Override
	public void removeCategory(String token, String nodeId, String catId) throws VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodeId, catId });
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;
		String catPath = null;
		String catUuid = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}
			
			if (PathUtils.isPath(catId)) {
				catPath = catId;
				catUuid = NodeBaseDAO.getInstance().getUuidFromPath(catId);
			} else {
				catPath = NodeBaseDAO.getInstance().getPathFromUuid(catId);
				catUuid = catId;
			}
			
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(nodeUuid);
			NodeBaseDAO.getInstance().removeCategory(nodeUuid, catUuid);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "REMOVE_CATEGORY", null);
			
			// Activity log
			UserActivity.log(auth.getName(), "REMOVE_CATEGORY", nodeUuid, nodePath, catPath);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("removeCategory: void");
	}
	
	@Override
	public String addKeyword(String token, String nodeId, String keyword) throws VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("addKeyword({}, {}, {})", new Object[] { token, nodeId, keyword });
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}
			
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(nodeUuid);
			
			if (keyword != null) {
				if (Config.SYSTEM_KEYWORD_LOWERCASE) {
					keyword = keyword.toLowerCase();
				}
				
				keyword = Encode.forHtml(keyword);
				NodeBaseDAO.getInstance().addKeyword(nodeUuid, keyword);
				
				// Check subscriptions
				BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "ADD_KEYWORD", null);
				
				// Activity log
				UserActivity.log(auth.getName(), "ADD_KEYWORD", nodeUuid, nodePath, keyword);
			}
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("addKeyword: {}", keyword);
		return keyword;
	}
	
	@Override
	public void removeKeyword(String token, String nodeId, String keyword) throws VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodeId, keyword });
		Authentication auth = null, oldAuth = null;
		String nodePath = null;
		String nodeUuid = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				auth = PrincipalUtils.getAuthentication();
			} else {
				oldAuth = PrincipalUtils.getAuthentication();
				auth = PrincipalUtils.getAuthenticationByToken(token);
			}
			
			if (PathUtils.isPath(nodeId)) {
				nodePath = nodeId;
				nodeUuid = NodeBaseDAO.getInstance().getUuidFromPath(nodeId);
			} else {
				nodePath = NodeBaseDAO.getInstance().getPathFromUuid(nodeId);
				nodeUuid = nodeId;
			}
			
			NodeBase nNode = NodeBaseDAO.getInstance().findByPk(nodeUuid);
			NodeBaseDAO.getInstance().removeKeyword(nodeUuid, keyword);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(nNode, auth.getName(), "REMOVE_KEYWORD", null);
			
			// Activity log
			UserActivity.log(auth.getName(), "REMOVE_KEYWORD", nodeUuid, nodePath, keyword);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token != null) {
				PrincipalUtils.setAuthentication(oldAuth);
			}
		}
		
		log.debug("removeCategory: void");
	}
}
