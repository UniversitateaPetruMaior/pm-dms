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

package com.openkm.vernum;

import org.hibernate.Query;
import org.hibernate.Session;

import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;

/**
 * @author pavila
 */
public class MajorMinorVersionNumerationAdapter implements VersionNumerationAdapter {
	public static final int MAJOR = 1;
	
	@Override
	public String getInitialVersionNumber() {
		return "1.0";
	}

	@Override
	public String getNextVersionNumber(Session session, NodeDocument nDoc, NodeDocumentVersion nDocVer, int increment) {
		String versionNumber = nDocVer.getName();
		String ver[] = versionNumber.split("\\.");
		int major = Integer.parseInt(ver[0]);
		int minor = Integer.parseInt(ver[1]);
		Query q = session.createQuery(qs);
		NodeDocumentVersion ndv = null;
		
		do {
			if (increment == MAJOR) {
				major++;
				minor = 0;
			} else {
				minor++;
			}
			
			q.setString("parent", nDoc.getUuid());
			q.setString("name", major + "." + minor);
			ndv = (NodeDocumentVersion) q.setMaxResults(1).uniqueResult();
		} while (ndv != null);
		
		return major + "." + minor;
	}
}
