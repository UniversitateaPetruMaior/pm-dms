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

public class ProfileMenuBookmark implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean manageBookmarksVisible;
	private boolean addBookmarkVisible;
	private boolean setHomeVisible;
	private boolean goHomeVisible;
	
	public boolean isManageBookmarksVisible() {
		return manageBookmarksVisible;
	}

	public void setManageBookmarksVisible(boolean manageBookmarksVisible) {
		this.manageBookmarksVisible = manageBookmarksVisible;
	}

	public boolean isAddBookmarkVisible() {
		return addBookmarkVisible;
	}

	public void setAddBookmarkVisible(boolean addBookmarkVisible) {
		this.addBookmarkVisible = addBookmarkVisible;
	}

	public boolean isSetHomeVisible() {
		return setHomeVisible;
	}

	public void setSetHomeVisible(boolean setHomeVisible) {
		this.setHomeVisible = setHomeVisible;
	}

	public boolean isGoHomeVisible() {
		return goHomeVisible;
	}

	public void setGoHomeVisible(boolean goHomeVisible) {
		this.goHomeVisible = goHomeVisible;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("manageBookmarksVisible="); sb.append(manageBookmarksVisible);
		sb.append(", addBookmarkVisible="); sb.append(addBookmarkVisible);
		sb.append(", setHomeVisible="); sb.append(setHomeVisible);
		sb.append(", goHomeVisible="); sb.append(goHomeVisible);
		sb.append("}");
		return sb.toString();
	}
}
