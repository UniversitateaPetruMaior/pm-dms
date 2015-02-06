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

package com.openkm.extension.frontend.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.openkm.frontend.client.bean.extension.GWTRoom;

/**
 * OKMOpenMeetingsServiceAsync
 * 
 * @author jllort
 * 
 */
public interface OKMOpenMeetingsServiceAsync extends RemoteService {
	public void loginUser(AsyncCallback<String> callback);
	
	public void getRoomsPublic(String SID, long roomType, AsyncCallback<List<GWTRoom>> callback);
	
	public void getPublicRoomURL(String SID, long roomId, boolean moderator, boolean showAudioVideoTest, String lang,
			AsyncCallback<String> callback);
	
	public void createNewRoom(String SID, String name, long roomType, long numberOfPartizipants, boolean isPublic,
			boolean appointment, boolean moderated, boolean allowUserQuestions, boolean audioOnly,
			boolean waitForRecording, boolean allowRecording, boolean topBar, AsyncCallback<?> callback);
	
	public void getRestrictedUserRooms(String SID, AsyncCallback<List<GWTRoom>> callback);
	
	public void getInterviewUserRooms(String SID, AsyncCallback<List<GWTRoom>> callback);
	
	public void deleteRoom(String SID, long rooms_id, AsyncCallback<?> callback);
	
	public void getPrivateConferenceUserRooms(String SID, AsyncCallback<List<GWTRoom>> callback);
	
	public void sendInvitation(String SID, long room_id, String users, String roles, String subject, String message,
			String lang, AsyncCallback<?> callback);
	
	public void addDocumentToRoom(String SID, long roomId, String path, AsyncCallback<?> callback);
}