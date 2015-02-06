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
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.widget.filebrowser.GWTFilter;

/**
 * @author jllort
 *
 */
public interface OKMMailServiceAsync {
	public void getChilds(String fldPath, Map<String, GWTFilter> mapFilter, AsyncCallback<List<GWTMail>> callback);
	public void delete(String mailPath, AsyncCallback<?> callback);
	public void move(String docPath, String destPath, AsyncCallback<?> callback);
	public void purge(String mailPath, AsyncCallback<?> callback);
	public void copy(String mailPath, String fldPath, AsyncCallback<?> callback);
	public void getProperties(String mailPath, AsyncCallback<GWTMail> callback);
	public void rename(String mailId, String newName, AsyncCallback<GWTMail> callback);
	public void isValid(String mailPath, AsyncCallback<Boolean> callback);
	public void forwardMail(String mailPath, String mails, String users, String roles, String message, AsyncCallback<?> callback);
}