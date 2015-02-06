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

package com.openkm.rest.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.AppVersion;
import com.openkm.bean.Folder;
import com.openkm.module.ModuleManager;
import com.openkm.module.RepositoryModule;
import com.openkm.rest.GenericException;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class RepositoryService {
	private static Logger log = LoggerFactory.getLogger(RepositoryService.class);
	
	@GET
	@Path("/getRootFolder")
	public Folder getRootFolder() throws GenericException {
		try {
			log.debug("getRootFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getRootFolder(null);
			log.debug("getRootFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getTrashFolder")
	public Folder getTrashFolder() throws GenericException {
		try {
			log.debug("getTrashFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getTrashFolder(null);
			log.debug("getTrashFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getTemplatesFolder")
	public Folder getTemplatesFolder() throws GenericException {
		try {
			log.debug("getTemplatesFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getTemplatesFolder(null);
			log.debug("getTemplatesFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getPersonalFolder")
	public Folder getPersonalFolder() throws GenericException {
		try {
			log.debug("getPersonalFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getPersonalFolder(null);
			log.debug("getPersonalFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getMailFolder")
	public Folder getMailFolder() throws GenericException {
		try {
			log.debug("getMailFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getMailFolder(null);
			log.debug("getMailFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getThesaurusFolder")
	public Folder getThesaurusFolder() throws GenericException {
		try {
			log.debug("getThesaurusFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getThesaurusFolder(null);
			log.debug("getThesaurusFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getCategoriesFolder")
	public Folder getCategoriesFolder() throws GenericException {
		try {
			log.debug("getCategoriesFolder()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			Folder fld = rm.getCategoriesFolder(null);
			log.debug("getCategoriesFolder: {}", fld);
			return fld;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@DELETE
	@Path("/purgeTrash")
	public void purgeTrash() throws GenericException {
		try {
			log.debug("purgeTrash()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			rm.purgeTrash(null);
			log.debug("purgeTrash: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getUpdateMessage")
	public String getUpdateMessage() throws GenericException {
		try {
			log.debug("getUpdateMessage()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String msg = rm.getUpdateMessage(null);
			log.debug("getUpdateMessage: {}", msg);
			return msg;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getRepositoryUuid")
	public String getRepositoryUuid() throws GenericException {
		try {
			log.debug("getRepositoryUuid()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String uuid = rm.getRepositoryUuid(null);
			log.debug("getRepositoryUuid: {}", uuid);
			return uuid;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/hasNode")
	public boolean hasNode(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("hasNode()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			boolean has = rm.hasNode(null, nodeId);
			log.debug("hasNode: {}", has);
			return has;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getNodePath/{uuid}")
	public String getNodePath(@PathParam("uuid") String uuid) throws GenericException {
		try {
			log.debug("getNodePath()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String path = rm.getNodePath(null, uuid);
			log.debug("getNodePath: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getNodeUuid")
	public String getNodeUuid(@QueryParam("nodePath") String nodePath) throws GenericException {
		try {
			log.debug("getNodeUuid()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			String path = rm.getNodeUuid(null, nodePath);
			log.debug("getNodeUuid: {}", path);
			return path;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/getAppVersion")
	public AppVersion getAppVersion() throws GenericException {
		try {
			log.debug("getAppVersion()");
			RepositoryModule rm = ModuleManager.getRepositoryModule();
			AppVersion ver = rm.getAppVersion(null);
			log.debug("getAppVersion: {}", ver);
			return ver;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
