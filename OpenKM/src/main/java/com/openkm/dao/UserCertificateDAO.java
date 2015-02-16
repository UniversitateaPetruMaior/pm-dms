package com.openkm.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.UserCertificate;

public class UserCertificateDAO {
	private static Logger log = LoggerFactory.getLogger(UserCertificateDAO.class);

	private UserCertificateDAO() {}
	
	/**
	 * Create 
	 */
	public static void create(UserCertificate uc) throws DatabaseException {
		log.debug("create({})", uc);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(uc);
			HibernateUtil.commit(tx);
		} catch(HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("create: void");
	}
	
	/**
	 * Delete
	 */
	public static void delete(int ucId) throws DatabaseException {
		log.debug("delete({})", ucId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			UserCertificate uc = (UserCertificate) session.load(UserCertificate.class, ucId);
			session.delete(uc);
			HibernateUtil.commit(tx);
		} catch(HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteUserCertificate: void");
	}

	/**
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<UserCertificate> findByUser(String user) throws DatabaseException {
		log.debug("findByUser({})", user);
		String qs = "from UserCertificate uc where uc.user=:user order by uc.id";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			List<UserCertificate> ret = q.list();
			log.debug("findByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static UserCertificate findByUser(String user, String certHash) throws DatabaseException {
		log.debug("findByUser({}, {})", user, certHash);
		String qs = "from UserCertificate uc where uc.user=:user and uc.certHash=:certHash order by uc.id";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", user);
			q.setString("certHash", certHash);
			UserCertificate ret = (UserCertificate)q.setMaxResults(1).uniqueResult();
			log.debug("findByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by pk
	 */
	public static UserCertificate findByPk(int ucId) throws DatabaseException {
		log.debug("findByPk({})", ucId);
		String qs = "from UserCertificate uc where uc.id=:id";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", ucId);
			UserCertificate ret = (UserCertificate) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}	
}
