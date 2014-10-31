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

package com.openkm.frontend.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTBookmark;

/**
 * @author jllort
 *
 */
public interface OKMBookmarkServiceAsync {
	public void getAll(AsyncCallback<List<GWTBookmark>> callback);
	public void add(String nodePath, String name, AsyncCallback<GWTBookmark> callback);
	public void remove(int bmId, AsyncCallback<?> callback);
	public void rename(int bmId, String newName, AsyncCallback<GWTBookmark> callback);
	public void get(int bmId, AsyncCallback<GWTBookmark> callback);
}