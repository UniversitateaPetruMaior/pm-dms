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

package com.openkm.automation.validation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.automation.AutomationUtils;
import com.openkm.automation.Validation;
import com.openkm.dao.NodeBaseDAO;

/**
 * HasPropertyGroupValue
 * 
 * @author jllort
 *
 */
public class HasPropertyGroupValue implements Validation {
	private static Logger log = LoggerFactory.getLogger(HasPropertyGroupValue.class);
	
	@Override
	public boolean isValid(HashMap<String, Object> env, Object... params) {
		String prpName = AutomationUtils.getString(0, params);
		String value = AutomationUtils.getString(1, params);
		String uuid = AutomationUtils.getUuid(env);
		
		try {
			if (uuid != null) {
				String grpName = prpName.substring(0, prpName.indexOf("."));
				grpName = grpName.replace("okp", "okg");
				Map<String, String> props = NodeBaseDAO.getInstance().getProperties(uuid, grpName);
				String propValue = props.get(prpName);
				
				if (propValue != null) {
					return propValue.equals(value);
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
