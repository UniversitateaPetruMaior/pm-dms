/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2014  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.bean.NodeBase;
import com.openkm.dao.bean.NodeSignature;
import com.openkm.module.db.stuff.SecurityHelper;

public class NodeSignatureDAO {
	private static Logger log = LoggerFactory.getLogger(NodeSignatureDAO.class);
	private static NodeSignatureDAO single = new NodeSignatureDAO();
	
	private NodeSignatureDAO() {
	}
	
	public static NodeSignatureDAO getInstance() {
		return single;
	}
	
	/**
	 * Find by parent
	 */
	@SuppressWarnings("unchecked")
	public List<NodeSignature> findByParent(String parentUuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByParent({})", parentUuid);
		String qs = "from NodeSignature ns where ns.parent=:parent order by ns.created";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			// Security Check
			NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
			SecurityHelper.checkRead(parentNode);
			
			Query q = session.createQuery(qs);
			q.setString("parent", parentUuid);
			List<NodeSignature> ret = q.list();
			initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByParent: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by path
	 */
	public NodeSignature findByPk(String uuid) throws PathNotFoundException, DatabaseException {
		log.debug("findByPk({})", uuid);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			// Security Check
			NodeBase parentNode = getParentNode(session, uuid);
			SecurityHelper.checkRead(parentNode);
			
			NodeSignature ret = (NodeSignature) session.load(NodeSignature.class, uuid);
			initialize(ret);
			HibernateUtil.commit(tx);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Create
	 */
	public void create(NodeSignature nSignature) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("create({})", nSignature);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			// Security Check
			NodeBase parentNode = (NodeBase) session.load(NodeBase.class, nSignature.getParent());
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);
			
			session.save(nSignature);
			HibernateUtil.commit(tx);
			log.debug("create: void");
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Delete
	 */
	public void delete(String uuid) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("delete({})", uuid);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			// Security Check
			NodeBase parentNode = getParentNode(session, uuid);
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);
			
			NodeSignature nSignature = (NodeSignature) session.load(NodeSignature.class, uuid);
			session.delete(nSignature);
			HibernateUtil.commit(tx);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("delete: void");
	}
	
	/**
	 * Update
	 */
	public void update(NodeSignature nSignature) throws PathNotFoundException, AccessDeniedException, DatabaseException {
		log.debug("update({})", nSignature);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			// Security Check
			NodeBase parentNode = getParentNode(session, nSignature.getUuid());
			SecurityHelper.checkRead(parentNode);
			SecurityHelper.checkWrite(parentNode);
			
			session.update(nSignature);
			HibernateUtil.commit(tx);
		} catch (PathNotFoundException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (AccessDeniedException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (DatabaseException e) {
			HibernateUtil.rollback(tx);
			throw e;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("update: void");
	}
	
	/**
	 * Purge in depth helper
	 */
	@SuppressWarnings("unchecked")
	public void purgeHelper(Session session, String parentUuid) throws HibernateException {
		String qs = "from NodeSignature ns where ns.parent=:parent";
		Query q = session.createQuery(qs);
		q.setString("parent", parentUuid);
		List<NodeSignature> listSignatures = q.list();
		
		for (NodeSignature nSignature : listSignatures) {
			session.delete(nSignature);
		}
	}
	
	/**
	 * Get parent node
	 */
	public NodeBase getParentNode(String uuid) throws DatabaseException {
		log.debug("getParentNode({})", uuid);
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			NodeBase parentNode = getParentNode(session,uuid);
			NodeBaseDAO.getInstance().initialize(parentNode);
			log.debug("getParentNode: {}", parentNode);
			return parentNode;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Get parent node
	 */
	private NodeBase getParentNode(Session session, String uuid) throws HibernateException {
		log.debug("getParentNode({}, {})", session, uuid);
		String qs = "select ns.parent from NodeSignature ns where ns.uuid=:uuid";
		Query q = session.createQuery(qs);
		q.setString("uuid", uuid);
		String parentUuid = (String) q.setMaxResults(1).uniqueResult();
		NodeBase parentNode = (NodeBase) session.load(NodeBase.class, parentUuid);
		log.debug("getParentNode: {}", parentNode);
		return parentNode;
	}
	
	/**
	 * Force initialization of a proxy
	 */
	private void initialize(NodeSignature nSignature) {
		if (nSignature != null) {
			Hibernate.initialize(nSignature);
		}
	}
	
	/**
	 * Force initialization of a proxy
	 */
	private void initialize(List<NodeSignature> nSignatureList) {
		for (NodeSignature nSignature : nSignatureList) {
			initialize(nSignature);
		}
	}
}
