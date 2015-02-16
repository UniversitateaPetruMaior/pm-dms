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

import com.openkm.bean.Signature;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeSignatureDAO;
import com.openkm.dao.bean.NodeSignature;

public class BaseSignatureModule {
	private static Logger log = LoggerFactory.getLogger(BaseSignatureModule.class);
	
	/**
	 * Create a new signature
	 */
	public static NodeSignature create(String parentUuid, String user, String signContent, String signDigest, String signSHA1, long signSize) throws PathNotFoundException,
			AccessDeniedException, DatabaseException {
		NodeSignature nSignature = new NodeSignature();
		nSignature.setUuid(UUID.randomUUID().toString());
		nSignature.setParent(parentUuid);
		nSignature.setUser(user);
		nSignature.setCreated(Calendar.getInstance());
		nSignature.setSignContent(signContent);
		nSignature.setSignDigest(signDigest);
		nSignature.setSignSHA1(signSHA1);
		nSignature.setSignSize(signSize);
		
		NodeSignatureDAO.getInstance().create(nSignature);
		return nSignature;
	}
	
	/**
	 * Get properties
	 */
	public static Signature getProperties(NodeSignature nSignature, String signaturePath) {
		log.debug("getProperties({})", nSignature);
		Signature signature = new Signature();
		
		// Properties
		signature.setDate(nSignature.getCreated());
		signature.setPath(signaturePath);
		signature.setUser(nSignature.getUser());
		signature.setSignContent(nSignature.getSignContent());
		signature.setSignDigest(nSignature.getSignDigest());
		signature.setSignSHA1(nSignature.getSignSHA1());
		signature.setSignSize(nSignature.getSignSize());
		
		log.debug("getProperties: {}", signature);
		return signature;
	}
}
