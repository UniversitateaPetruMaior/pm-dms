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
import com.openkm.bean.Permission;
import com.openkm.dao.NodeBaseDAO;

/**
 * RevokeAllRoles
 * 
 * @author jllort
 *
 */
public class RevokeAllRoles  implements Action {
	private static Logger log = LoggerFactory.getLogger(RevokeAllRoles.class);
	
	@Override
	public void executePre(HashMap<String, Object> env, Object... params) {
	}
	
	@Override
	public void executePost(HashMap<String, Object> env, Object... params) {
		boolean recursive = AutomationUtils.getBoolean(0, params).booleanValue();
		String uuid = AutomationUtils.getUuid(env);
		int allGrants = Permission.ALL_GRANTS;
		try {
			if (uuid != null) {
				for (String role : NodeBaseDAO.getInstance().getRolePermissions(uuid).keySet()) {
					NodeBaseDAO.getInstance().revokeRolePermissions(uuid, role, allGrants, recursive);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
