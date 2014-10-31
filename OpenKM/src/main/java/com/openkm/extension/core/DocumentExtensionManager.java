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

package com.openkm.extension.core;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ServiceConfigurationError;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Version;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.Ref;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;

public class DocumentExtensionManager {
	private static Logger log = LoggerFactory.getLogger(DocumentExtensionManager.class);
	private static DocumentExtensionManager service = null;
	
	private DocumentExtensionManager() {
	}
	
	public static synchronized DocumentExtensionManager getInstance() {
		if (service == null) {
			service = new DocumentExtensionManager();
		}
		
		return service;
	}
	
	/**
	 * Handle PRE create extensions
	 */
	public void preCreate(Session session, Ref<Node> parentNode, Ref<File> content, Ref<Document> doc)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException {
		log.debug("preCreate({}, {}, {}, {})", new Object[] { session, parentNode, content, doc });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preCreate(session, parentNode, content, doc);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Handle POST create extensions
	 */
	public void postCreate(Session session, Ref<Node> parentNode, Ref<Node> docNode)
			throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, IOException, DatabaseException, ExtensionException {
		log.debug("postCreate({}, {}, {})", new Object[] { session, parentNode, docNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postCreate(session, parentNode, docNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Handle PRE move extensions
	 */
	public void preMove(Session session, Ref<Node> srcDocNode, Ref<Node> dstFldNode) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException, ExtensionException {
		log.debug("preMove({}, {}, {})", new Object[] { session, srcDocNode, dstFldNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preMove(session, srcDocNode, dstFldNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Handle POST move extensions
	 * 
	 * @param oldDocPath - original docPath
	 */
	public void postMove(Session session, String oldDocPath, Ref<Node> srcFldNode, Ref<Node> dstDocNode)
			throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException {
		log.debug("postMove({}, {}, {}, {})", new Object[] { session, oldDocPath, srcFldNode, dstDocNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postMove(session, oldDocPath, srcFldNode, dstDocNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preDelete(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, DatabaseException, ExtensionException {
		log.debug("preDelete({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preDelete(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postDelete(Session session, String fileName) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, LockException, DatabaseException, ExtensionException {
		log.debug("postDelete({}, {})", new Object[] { session, fileName });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postDelete(session, fileName);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preSetContent(Session session, Ref<Node> refDocumentNode) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException {
		log.debug("preSetContent({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preSetContent(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postSetContent(Session session, Ref<Node> refDocumentNode) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException {
		log.debug("postSetContent({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postSetContent(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preRename(Session session, String docPath, String newPath, Ref<Node> refDocumentNode)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException {
		log.debug("preRename({}, {}, {}, {})", new Object[] { session, docPath, newPath, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preRename(session, docPath, newPath, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postRename(Session session, String docPath, String newPath, Ref<Node> refDocumentNode)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, ItemExistsException,
			DatabaseException, ExtensionException {
		log.debug("postRename({}, {}, {}, {})", new Object[] { session, docPath, newPath, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postRename(session, docPath, newPath, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preCheckin(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, VersionException, DatabaseException,
			ExtensionException {
		log.debug("preCheckin({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preCheckin(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postCheckin(Session session, Ref<Node> refDocumentNode, Ref<Version> refVersion)
			throws AccessDeniedException, RepositoryException, PathNotFoundException, LockException, VersionException,
			DatabaseException, ExtensionException {
		log.debug("postCheckin({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postCheckin(session, refDocumentNode, refVersion);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void prePurge(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException, ExtensionException {
		log.debug("prePurge({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.prePurge(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postPurge(Session session, String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException, ExtensionException {
		log.debug("postPurge({}, {})", new Object[] { session, docPath });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postPurge(session, docPath);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preCopy(Session session, Ref<Node> refSrcNode, Ref<Node> refDstFolderNode) throws ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			UserQuotaExceededException, ExtensionException {
		log.debug("preCopy({}, {}, {})", new Object[] { session, refSrcNode, refDstFolderNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preCopy(session, refSrcNode, refDstFolderNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postCopy(Session session, Ref<Node> refSrcNode, Ref<Node> refNewDocument, Ref<Node> refDstFolderNode)
			throws ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException, ExtensionException {
		log.debug("postCopy({}, {}, {}, {})", new Object[] { session, refSrcNode, refNewDocument, refDstFolderNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postCopy(session, refSrcNode, refNewDocument, refDstFolderNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void preRestoreVersion(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException, ExtensionException {
		log.debug("preRestoreVersion({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preRestoreVersion(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void postRestoreVersion(Session session, Ref<Node> refDocumentNode) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException, ExtensionException {
		log.debug("postRestoreVersion({}, {})", new Object[] { session, refDocumentNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<DocumentExtension> col = em.getPlugins(DocumentExtension.class);
			Collections.sort(col, new OrderComparator<DocumentExtension>());
			
			for (DocumentExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postRestoreVersion(session, refDocumentNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
}
