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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Note;
import com.openkm.module.ModuleManager;
import com.openkm.module.NoteModule;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.NoteList;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class NoteService {
	private static Logger log = LoggerFactory.getLogger(NoteService.class);
	
	@POST
	@Path("/add")
	// The "text" parameter comes in the POST request body.
	public Note add(@QueryParam("nodeId") String nodeId, String text) throws GenericException {
		try {
			log.debug("add({}, {})", new Object[] { nodeId, text });
			NoteModule nm = ModuleManager.getNoteModule();
			Note ret = nm.add(null, nodeId, text);
			log.debug("addNote: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/get")
	public Note get(@QueryParam("noteId") String noteId) throws GenericException {
		try {
			log.debug("get({})", noteId);
			NoteModule nm = ModuleManager.getNoteModule();
			Note ret = nm.get(null, noteId);
			log.debug("get: {}", ret);
			return ret;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@DELETE
	@Path("/delete")
	public void delete(@QueryParam("noteId") String noteId) throws GenericException {
		try {
			log.debug("remove({})", noteId);
			NoteModule nm = ModuleManager.getNoteModule();
			nm.delete(null, noteId);
			log.debug("remove: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@PUT
	@Path("/set")
	// The "text" parameter comes in the PUT request body.
	public void set(@QueryParam("noteId") String noteId, String text) throws GenericException {
		try {
			log.debug("set({}, {})", new Object[] { noteId, text });
			NoteModule nm = ModuleManager.getNoteModule();
			nm.set(null, noteId, text);
			log.debug("set: void");
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
	
	@GET
	@Path("/list")
	public NoteList list(@QueryParam("nodeId") String nodeId) throws GenericException {
		try {
			log.debug("list({})", nodeId);
			NoteModule nm = ModuleManager.getNoteModule();
			NoteList nl = new NoteList();
			nl.getList().addAll(nm.list(null, nodeId));
			log.debug("list: {}", nl);
			return nl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
