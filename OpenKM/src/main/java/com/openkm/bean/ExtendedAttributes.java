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

package com.openkm.bean;

public class ExtendedAttributes {
	private boolean categories = false;
	private boolean keywords = false;
	private boolean propertyGroups = false;
	private boolean notes = false;
	
	public boolean isCategories() {
		return categories;
	}

	public void setCategories(boolean categories) {
		this.categories = categories;
	}

	public boolean isKeywords() {
		return keywords;
	}

	public void setKeywords(boolean keywords) {
		this.keywords = keywords;
	}

	public boolean isPropertyGroups() {
		return propertyGroups;
	}

	public void setPropertyGroups(boolean propertyGroups) {
		this.propertyGroups = propertyGroups;
	}

	public boolean isNotes() {
		return notes;
	}

	public void setNotes(boolean notes) {
		this.notes = notes;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("categories=").append(categories);
		sb.append(", keywords=").append(keywords);
		sb.append(", propertyGroups=").append(propertyGroups);
		sb.append(", notes=").append(notes);
		sb.append("}");
		return sb.toString();
	}
}
