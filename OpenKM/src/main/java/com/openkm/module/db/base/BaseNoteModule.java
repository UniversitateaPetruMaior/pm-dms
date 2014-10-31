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

package com.openkm.module.db.base;

import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Note;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeNoteDAO;
import com.openkm.dao.bean.NodeNote;

public class BaseNoteModule {
	private static Logger log = LoggerFactory.getLogger(BaseNoteModule.class);
	
	/**
	 * Create a new note
	 */
	public static NodeNote create(String parentUuid, String user, String text) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		NodeNote nNote = new NodeNote();
		nNote.setUuid(UUID.randomUUID().toString());
		nNote.setParent(parentUuid);
		nNote.setAuthor(user);
		nNote.setCreated(Calendar.getInstance());
		nNote.setText(text);
		NodeNoteDAO.getInstance().create(nNote);
		return nNote;
	}
	
	/**
	 * Get properties
	 */
	public static Note getProperties(NodeNote nNote, String notePath) {
		log.debug("getProperties({})", nNote);
		Note note = new Note();
		
		// Properties
		note.setDate(nNote.getCreated());
		note.setAuthor(nNote.getAuthor());
		note.setText(nNote.getText());
		note.setPath(notePath);
		
		log.debug("getProperties: {}", note);
		return note;
	}
}
