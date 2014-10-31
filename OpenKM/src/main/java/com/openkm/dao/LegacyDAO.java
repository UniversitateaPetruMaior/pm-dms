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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;

public class LegacyDAO {
	private static Logger log = LoggerFactory.getLogger(LegacyDAO.class);
	private static DataSource ds = null;
	
	/**
	 * Return JDBC Connection
	 */
	public static Connection getConnection() {
		try {
			if (ds == null) {
				log.info("Looking for {} DataSource...", Config.HIBERNATE_DATASOURCE);
				Context ctx = new InitialContext();
				ds = (DataSource) ctx.lookup(Config.HIBERNATE_DATASOURCE);
				ctx.close();
			}
			
			return ds.getConnection();
		} catch (NamingException e) {
			log.error("DataSource not found: {}", e.getMessage());
			throw new RuntimeException(e);
		} catch (SQLException e) {
			log.error("Can't get connection from DataSource", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Execute sentence
	 */
	public static void execute(Connection con, String sql) throws IOException, SQLException {
		Statement stmt = con.createStatement();
		
		try {
			log.info("execute: {}", sql);
			stmt.execute(sql);
		} finally {
			close(stmt);
		}
	}
	
	/**
	 * Execute script
	 */
	public static List<HashMap<String, String>> executeScript(Connection con, Reader file) throws 
			IOException, SQLException {
		List<HashMap<String, String>> errors = new ArrayList<HashMap<String, String>>();
		BufferedReader br = new BufferedReader(file);
		Statement stmt = con.createStatement();
		String sql = null;
		int lineNo = 0;
		
		try {
			while ((sql = br.readLine()) != null) {
				String trimmedSql = sql.trim();
				lineNo++;
				
				if (trimmedSql.length() > 0 && !trimmedSql.startsWith("--")) {
					try {
						if (trimmedSql.endsWith(";")) {
							trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
						}
						
						stmt.execute(trimmedSql);
					} catch (SQLException e) {
						HashMap<String, String> error = new HashMap<String, String>();
						error.put("ln", Integer.toString(lineNo));
						error.put("sql", trimmedSql);
						error.put("msg", e.getMessage());
						errors.add(error);
					}
				}
			}
		} finally {
			close(stmt);
		}
		
		return errors;
	}
	
	/**
	 * Convenient method to close connections
	 */
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.warn("Error closing connection: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Convenient method to close resultset
	 */
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("Error closing resultset: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Convenient method to close statements
	 */
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.warn("Error closing statement: " + e.getMessage(), e);
			}
		}	
	}
	
	/**
	 * Execute query
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> executeQuery(String query) throws DatabaseException {
		log.debug("executeValueQuery({})", query);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);
			List<Object> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("executeValueQuery: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Execute query
	 */
	public static Object executeQueryUnique(String query) throws DatabaseException {
		log.debug("executeQueryUnique({})", query);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(query);
			Object ret = q.uniqueResult();
			HibernateUtil.commit(tx);
			log.debug("executeQueryUnique: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Utility inner class
	 */
	public static class ResultWorker implements Work {
		private List<String> values = new ArrayList<String>();
		private String sql = null;
		
		public void setSql(String sql) {
			this.sql = sql;
		}
		
		public List<String> getValues() {
			return values;
		}
		
		@Override
		public void execute(Connection con) throws SQLException {
			Statement st = null;
			ResultSet rs = null;
			
			if (sql != null && !sql.isEmpty()) {
				try {
					st = con.createStatement();
					rs = st.executeQuery(sql);
					
					while (rs.next()) {
						values.add(rs.getString(1));
					}
				} finally {	
					close(rs);
					close(st);
				}
			}
		}
	}
}
