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

package com.openkm.dao.bean;

import java.io.Serializable;

public class ProfileTabMail implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean propertiesVisible;
	private boolean previewVisible;
	private boolean securityVisible;
	private boolean notesVisible;
	
	public boolean isPropertiesVisible() {
		return propertiesVisible;
	}

	public void setPropertiesVisible(boolean propertiesVisible) {
		this.propertiesVisible = propertiesVisible;
	}

	public boolean isSecurityVisible() {
		return securityVisible;
	}

	public void setSecurityVisible(boolean securityVisible) {
		this.securityVisible = securityVisible;
	}

	public boolean isPreviewVisible() {
		return previewVisible;
	}

	public void setPreviewVisible(boolean previewVisible) {
		this.previewVisible = previewVisible;
	}
	
	public boolean isNotesVisible() {
		return notesVisible;
	}

	public void setNotesVisible(boolean notesVisible) {
		this.notesVisible = notesVisible;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("propertiesVisible="); sb.append(propertiesVisible);
		sb.append(", previewVisible="); sb.append(previewVisible);
		sb.append(", securityVisible="); sb.append(securityVisible);
		sb.append(", notesVisible="); sb.append(notesVisible);
		sb.append("}");
		return sb.toString();
	}
}
