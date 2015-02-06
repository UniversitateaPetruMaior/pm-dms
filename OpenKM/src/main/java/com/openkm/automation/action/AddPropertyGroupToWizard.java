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

import com.openkm.automation.Action;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.FileUploadResponse;

/**
 * AddPropertyGroupToWizard
 * 
 * @author jllort
 *
 */
public class AddPropertyGroupToWizard implements Action {	
	@Override
	public void executePre(HashMap<String, Object> env, Object... params) {
		execute(env, params);
	}
	
	@Override
	public void executePost(HashMap<String, Object> env, Object... params) {
		execute(env, params);
	}
	
	/**
	 * execute
	 * 
	 * @param env OpenKM API internal environment data.
	 * @param params Action configured parameters.
	 */
	private void execute(HashMap<String, Object> env, Object... params) {
		String group = (String) params[0];
		
		if (env.keySet().contains(AutomationUtils.UPLOAD_RESPONSE)) {
			FileUploadResponse fuResponse = (FileUploadResponse) env.get(AutomationUtils.UPLOAD_RESPONSE);
			fuResponse.getGroupsList().add(group);
		} else {
			FileUploadResponse fuResponse = new FileUploadResponse();
			fuResponse.getGroupsList().add(group);
			env.put(AutomationUtils.UPLOAD_RESPONSE, fuResponse);
		}
	}
}
