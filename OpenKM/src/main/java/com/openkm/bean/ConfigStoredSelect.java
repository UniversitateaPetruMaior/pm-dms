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

package com.openkm.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigStoredSelect implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<ConfigStoredOption> options = new ArrayList<ConfigStoredOption>();
	
	public List<ConfigStoredOption> getOptions() {
		return options;
	}
	
	public void setOptions(List<ConfigStoredOption> options) {
		this.options = options;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("options=").append(options);
		sb.append("}");
		return sb.toString();
	}
}
