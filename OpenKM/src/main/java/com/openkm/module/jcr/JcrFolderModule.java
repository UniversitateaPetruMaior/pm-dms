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
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.commons.lang.NotImplementedException;
import org.apache.jackrabbit.api.XASession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.ContentInfo;
import com.openkm.bean.ExtendedAttributes;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.Ref;
import com.openkm.core.RepositoryException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.extension.core.FolderExtensionManager;
import com.openkm.module.FolderModule;
import com.openkm.module.jcr.base.BaseFolderModule;
import com.openkm.module.jcr.base.BaseScriptingModule;
import com.openkm.module.jcr.stuff.JCRUtils;
import com.openkm.module.jcr.stuff.JcrSessionManager;
import com.openkm.util.PathUtils;
import com.openkm.util.UserActivity;

public class JcrFolderModule implements FolderModule {
	private static Logger log = LoggerFactory.getLogger(JcrFolderModule.class);
	
	@Override
	public Folder create(String token, Folder fld) throws AccessDeniedException, RepositoryException, PathNotFoundException,
			ItemExistsException, DatabaseException, ExtensionException {
		log.debug("create({}, {})", token, fld);
		Folder newFolder = null;
		Node parentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			String parent = PathUtils.getParent(fld.getPath());
			String name = PathUtils.getName(fld.getPath());
			parentNode = session.getRootNode().getNode(parent.substring(1));
			
			// Escape dangerous chars in name
			name = PathUtils.escape(name);
			fld.setPath(parent + "/" + name);
			
			// EP - PRE
			Ref<Node> refParentNode = new Ref<Node>(parentNode);
			Ref<Folder> refFld = new Ref<Folder>(fld);
			FolderExtensionManager.getInstance().preCreate(session, refParentNode, refFld);
			parentNode = refParentNode.get();
			name = PathUtils.escape(PathUtils.getName(refFld.get().getPath()));
			
			// Create node
			Node folderNode = BaseFolderModule.create(session, parentNode, name);
			
			// EP - POST
			Ref<Node> refFolderNode = new Ref<Node>(folderNode);
			FolderExtensionManager.getInstance().postCreate(session, refParentNode, refFolderNode);
			
			// Set returned folder properties
			newFolder = BaseFolderModule.getProperties(session, folderNode);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, folderNode, "CREATE_FOLDER");
			
			// Activity log
			UserActivity.log(session.getUserID(), "CREATE_FOLDER", folderNode.getUUID(), fld.getPath(), null);
		} catch (javax.jcr.PathNotFoundException e) {
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			JCRUtils.discardsPendingChanges(parentNode);
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(parentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(parentNode);
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			JCRUtils.discardsPendingChanges(parentNode);
			throw e;
		} catch (ExtensionException e) {
			JCRUtils.discardsPendingChanges(parentNode);
			throw e;
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("create: {}", newFolder);
		return newFolder;
	}
	
	@Override
	public Folder getProperties(String token, String fldPath) throws PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getProperties({}, {})", token, fldPath);
		Folder fld = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));
			fld = BaseFolderModule.getProperties(session, folderNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_FOLDER_PROPERTIES", folderNode.getUUID(), fldPath, null);
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
		
		log.debug("get: {}", fld);
		return fld;
	}
	
	@Override
	public void delete(String token, String fldPath) throws AccessDeniedException, RepositoryException, PathNotFoundException,
			LockException, DatabaseException {
		log.debug("delete({}, {})", token, fldPath);
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			String name = PathUtils.getName(fldPath);
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));
			Node parentNode = folderNode.getParent();
			Node userTrash = session.getRootNode().getNode(Repository.TRASH + "/" + session.getUserID());
			
			if (BaseFolderModule.hasLockedNodes(folderNode)) {
				throw new LockException("Can't delete a folder with child locked nodes");
			}
			
			if (!BaseFolderModule.hasWriteAccess(folderNode)) {
				throw new AccessDeniedException("Can't delete a folder with readonly nodes");
			}
			
			if (Repository.ROOT.equals(name) || Repository.CATEGORIES.equals(name) || Repository.THESAURUS.equals(name)
					|| Repository.TEMPLATES.equals(name) || Repository.PERSONAL.equals(name) || Repository.MAIL.equals(name)
					|| Repository.TRASH.equals(name)) {
				throw new AccessDeniedException("Can't delete a required node");
			}
			
			// Test if already exists a folder with the same name in the trash
			String destPath = userTrash.getPath() + "/";
			String testName = name;
			
			for (int i = 1; session.itemExists(destPath + testName); i++) {
				testName = name + " (" + i + ")";
			}
			
			// PRE
			Ref<Node> refFolderNode = new Ref<Node>(folderNode);
			FolderExtensionManager.getInstance().preDelete(session, fldPath, refFolderNode);
			
			session.move(folderNode.getPath(), destPath + testName);
			session.getRootNode().save();
			
			// POST
			FolderExtensionManager.getInstance().postDelete(session, fldPath, refFolderNode);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, folderNode, "DELETE_FOLDER");
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_FOLDER", folderNode.getUUID(), fldPath, null);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("delete: void");
	}
	
	@Override
	public void purge(String token, String fldPath) throws AccessDeniedException, RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("purge({}, {})", token, fldPath);
		Node parentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));
			String fldUuid = folderNode.getUUID();
			
			// PRE
			Ref<Node> refFolderNode = new Ref<Node>(folderNode);
			FolderExtensionManager.getInstance().prePurge(session, fldPath, refFolderNode);
			
			synchronized (folderNode) {
				parentNode = folderNode.getParent();
				BaseFolderModule.purge(session, folderNode);
				parentNode.save();
			}
			
			// POST
			FolderExtensionManager.getInstance().postPurge(session, fldPath);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, folderNode, "PURGE_FOLDER");
			
			// Activity log
			UserActivity.log(session.getUserID(), "PURGE_FOLDER", fldUuid, fldPath, null);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("purge: void");
	}
	
	@Override
	public Folder rename(String token, String fldPath, String newName) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, fldPath, newName });
		Folder renamedFolder = null;
		Session session = null;
		Node folderNode = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			String parent = PathUtils.getParent(fldPath);
			String name = PathUtils.getName(fldPath);
			
			// Escape dangerous chars in name
			newName = PathUtils.escape(newName);
			
			if (newName != null && !newName.equals("") && !newName.equals(name)) {
				String newPath = parent + "/" + newName;
				
				// PRE
				Ref<Node> refFolderNode = new Ref<Node>(folderNode);
				FolderExtensionManager.getInstance().preRename(session, fldPath, newPath, refFolderNode);
				
				session.move(fldPath, newPath);
				
				// Set new name
				folderNode = session.getRootNode().getNode(newPath.substring(1));
				folderNode.setProperty(Folder.NAME, newName);
				
				// Publish changes
				session.save();
				
				// Set returned folder properties
				renamedFolder = BaseFolderModule.getProperties(session, folderNode);
				
				// POST
				refFolderNode = new Ref<Node>(folderNode);
				FolderExtensionManager.getInstance().postRename(session, fldPath, newPath, refFolderNode);
			} else {
				// Don't change anything
				folderNode = session.getRootNode().getNode(fldPath.substring(1));
				renamedFolder = BaseFolderModule.getProperties(session, folderNode);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "RENAME_FOLDER", folderNode.getUUID(), fldPath, newName);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("rename: {}", renamedFolder);
		return renamedFolder;
	}
	
	@Override
	public void move(String token, String fldPath, String dstPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[] { token, fldPath, dstPath });
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			// Node fldNode = session.getRootNode().getNode(fldPath.substring(1));
			String name = PathUtils.getName(fldPath);
			String dstNodePath = dstPath + "/" + name;
			
			// PRE
			FolderExtensionManager.getInstance().preMove(session, fldPath, dstNodePath);
			
			session.move(fldPath, dstNodePath);
			session.save();
			Node dstFldNode = session.getRootNode().getNode(dstNodePath.substring(1));
			
			// POST
			Ref<Node> refDstFldNode = new Ref<Node>(dstFldNode);
			FolderExtensionManager.getInstance().postMove(session, fldPath, dstNodePath, refDstFldNode);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, dstFldNode.getParent(), dstFldNode, "MOVE_FOLDER");
			
			// Activity log
			UserActivity.log(session.getUserID(), "MOVE_FOLDER", dstFldNode.getUUID(), fldPath, dstPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("move: void");
	}
	
	@Override
	public void copy(String token, String fldPath, String dstPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, IOException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { token, fldPath, dstPath });
		// Transaction t = null;
		XASession session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = (XASession) JCRUtils.getSession();
			} else {
				session = (XASession) JcrSessionManager.getInstance().get(token);
			}
			
			String name = PathUtils.getName(fldPath);
			// t = new Transaction(session);
			// t.start();
			
			// Make some work
			Node srcFolderNode = session.getRootNode().getNode(fldPath.substring(1));
			Node dstFolderNode = session.getRootNode().getNode(dstPath.substring(1));
			
			// PRE
			Ref<Node> refSrcFolderNode = new Ref<Node>(srcFolderNode);
			Ref<Node> refDstFolderNode = new Ref<Node>(dstFolderNode);
			FolderExtensionManager.getInstance().preCopy(session, refSrcFolderNode, refDstFolderNode);
			
			Node newFolder = BaseFolderModule.create(session, dstFolderNode, name);
			dstFolderNode.save();
			BaseFolderModule.copy(session, srcFolderNode, newFolder);
			
			// t.end();
			// t.commit();
			
			// POST
			Ref<Node> refNewFolderNode = new Ref<Node>(newFolder);
			FolderExtensionManager.getInstance().postCopy(session, refSrcFolderNode, refNewFolderNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "COPY_FOLDER", dstFolderNode.getUUID(), fldPath, dstPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			// t.rollback();
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			// t.rollback();
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			// t.rollback();
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			// t.rollback();
			throw new RepositoryException(e.getMessage(), e);
		} catch (java.io.IOException e) {
			log.error(e.getMessage(), e);
			// t.rollback();
			throw e;
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public void extendedCopy(String token, String fldPath, String dstPath, ExtendedAttributes extAttr) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, DatabaseException, UserQuotaExceededException {
		throw new NotImplementedException("extendedCopy");
	}
	
	@Override
	@Deprecated
	public List<Folder> getChilds(String token, String fldPath) throws PathNotFoundException, RepositoryException, DatabaseException {
		return getChildren(token, fldPath);
	}
	
	@Override
	public List<Folder> getChildren(String token, String fldPath) throws PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getChildren({}, {})", token, fldPath);
		List<Folder> children = new ArrayList<Folder>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));
			
			for (NodeIterator ni = folderNode.getNodes(); ni.hasNext();) {
				Node child = ni.nextNode();
				
				if (child.isNodeType(Folder.TYPE)) {
					children.add(BaseFolderModule.getProperties(session, child));
				}
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_CHILDREN_FOLDERS", folderNode.getUUID(), fldPath, null);
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
		
		log.debug("getChildren: {}", children);
		return children;
	}
	
	@Override
	public ContentInfo getContentInfo(String token, String fldPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
		log.debug("getContentInfo({}, {})", token, fldPath);
		ContentInfo contentInfo = new ContentInfo();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));
			contentInfo = BaseFolderModule.getContentInfo(folderNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_FOLDER_CONTENT_INFO", folderNode.getUUID(), fldPath, contentInfo.toString());
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}
	
	@Override
	public boolean isValid(String token, String fldPath) throws PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, fldPath);
		boolean valid = false;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node node = session.getRootNode().getNode(fldPath.substring(1));
			
			if (node.isNodeType(Folder.TYPE)) {
				valid = true;
			}
		} catch (javax.jcr.PathNotFoundException e) {
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("isValid: {}", valid);
		return valid;
	}
	
	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getPath({}, {})", token, uuid);
		String path = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node node = session.getNodeByUUID(uuid);
			
			if (node.isNodeType(Folder.TYPE)) {
				path = node.getPath();
			}
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) {
				JCRUtils.logout(session);
			}
		}
		
		log.debug("getPath: {}", path);
		return path;
	}
}
