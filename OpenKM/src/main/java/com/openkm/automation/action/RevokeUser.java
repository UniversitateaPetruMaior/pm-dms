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

package com.openkm.automation.action;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.dao.NodeBaseDAO;

/**
 * RevokeUser
 * 
 * @author jllort
 *
 */
public class RevokeUser implements Action {
	private static Logger log = LoggerFactory.getLogger(RevokeUser.class);
	
	@Override
	public void executePre(HashMap<String, Object> env, Object... params) {
	}
	
	@Override
	public void executePost(HashMap<String, Object> env, Object... params) {
		String role = AutomationUtils.getString(0, params);
		Integer permissions = AutomationUtils.getInterger(1, params);
		String uuid = AutomationUtils.getUuid(env);
		
		try {
			if (uuid != null) {
				NodeBaseDAO.getInstance().revokeUserPermissions(uuid, role, permissions, false);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
