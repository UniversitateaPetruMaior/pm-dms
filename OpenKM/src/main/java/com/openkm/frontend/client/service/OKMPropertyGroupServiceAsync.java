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
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;

/**
 * @author jllort
 *
 */
public interface OKMPropertyGroupServiceAsync {
	public void getAllGroups(AsyncCallback<List<GWTPropertyGroup>> callback);
	public void getAllGroups(String path, AsyncCallback<List<GWTPropertyGroup>> callback);
	public void addGroup(String path, String grpName, AsyncCallback<?> callback);
	public void getGroups(String path, AsyncCallback<List<GWTPropertyGroup>> callback);
	public void getProperties(String path, String grpName, boolean suggestion, AsyncCallback<List<GWTFormElement>> callback);
	public void setProperties(String path, String grpName, List<GWTFormElement> formProperties, AsyncCallback<?> callback);
	public void removeGroup( String path, String grpName, AsyncCallback<?> callback);
	public void getPropertyGroupForm(String grpName, AsyncCallback<List<GWTFormElement>> callback);
	public void getPropertyGroupForm(String grpName, String path, boolean suggestion, AsyncCallback<List<GWTFormElement>> callback);
}