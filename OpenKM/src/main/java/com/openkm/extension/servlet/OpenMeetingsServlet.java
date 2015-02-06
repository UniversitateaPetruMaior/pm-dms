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

package com.openkm.extension.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.bean.openmeetings.Room;
import com.openkm.extension.frontend.client.service.OKMOpenMeetingsService;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTRoom;
import com.openkm.frontend.client.constants.service.ErrorCode;
import com.openkm.module.db.DbAuthModule;
import com.openkm.openmeetings.OpenMeetingsUtils;
import com.openkm.openmeetings.service.FileService;
import com.openkm.openmeetings.service.RoomService;
import com.openkm.openmeetings.service.UserService;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;

/**
 * OpenMeetingsServlet
 * 
 * @author jllort
 * 
 */
public class OpenMeetingsServlet extends OKMRemoteServiceServlet implements OKMOpenMeetingsService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(OpenMeetingsServlet.class);
	private static long externalFileId = 0; // File id when uploading
	
	@Override
	public String loginUser() throws OKMException {
		log.debug("loginUser()");
		updateSessionManager();
		try {
			return UserService.loginUser();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
	}
	
	@Override
	public List<GWTRoom> getRoomsPublic(String SID, long roomType) throws OKMException {
		log.debug("getRoomsPublic({},{})", SID, roomType);
		updateSessionManager();
		List<GWTRoom> rooms = new ArrayList<GWTRoom>();
		
		try {
			for (Room room : RoomService.getRoomsPublic(SID, roomType)) {
				rooms.add(OpenMeetingsUtils.copy(room));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		
		return rooms;
	}
	
	@Override
	public String getPublicRoomURL(String SID, long roomId, boolean moderator, boolean showAudioVideoTest, String lang)
			throws OKMException {
		log.debug("getRoomsPublic({},{},{},{},{})", new Object[] { SID, roomId, moderator, showAudioVideoTest, lang });
		updateSessionManager();
		String url = "";
		try {
			String roomHash = "";
			String userId = getThreadLocalRequest().getRemoteUser();
			String firstName;
			
			firstName = OKMAuth.getInstance().getName(null, userId);
			
			String lastName = "";
			String profilePictureUrl = "";
			String email = new DbAuthModule().getMail(null, userId);
			int becomeModeratorAsInt = 0;
			int showAudioVideoTestAsInt = 0;
			if (moderator) {
				becomeModeratorAsInt = 1;
			}
			if (showAudioVideoTest) {
				showAudioVideoTestAsInt = 1;
			}
			
			String externalUserType = getExternalUserType(userId);
			roomHash = UserService.setUserObjectAndGenerateRoomHashByURL(SID, userId, firstName, lastName,
					profilePictureUrl, email, userId, externalUserType, roomId, becomeModeratorAsInt,
					showAudioVideoTestAsInt);
			
			url = OpenMeetingsUtils.getOpenMeetingServerURL() + "/?scopeRoomId=" + String.valueOf(roomId)
					+ "&secureHash=" + roomHash + "&language=" + OpenMeetingsUtils.getOpenMeetingsLang(lang)
					+ "&lzproxied=solo";
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService,
					ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		return url;
	}
	
	@Override
	public void createNewRoom(String SID, String name, long roomType, long numberOfPartizipants, boolean isPublic,
			boolean appointment, boolean moderated, boolean allowUserQuestions, boolean audioOnly,
			boolean waitForRecording, boolean allowRecording, boolean topBar) throws OKMException {
		log.debug("createNewRoom({},{},{},{},{},{},{},{},{},{},{},{})", new Object[] { SID, name, roomType,
				numberOfPartizipants, isPublic, appointment, moderated, allowUserQuestions, audioOnly,
				waitForRecording, allowRecording, topBar });
		updateSessionManager();
		try {
			String externalRoomType = getRoomType(roomType);
			long roomId = RoomService.addRoomWithModerationExternalTypeAndTopBarOption(SID, name, roomType, "",
					numberOfPartizipants, isPublic, appointment, false, moderated, externalRoomType,
					allowUserQuestions, audioOnly, waitForRecording, allowRecording, topBar);
			
			// Case where creator should be added to the room
			if (roomType == GWTRoom.TYPE_RESTRICTED || roomType == GWTRoom.TYPE_INTERVIEW) {
				String userId = getThreadLocalRequest().getRemoteUser();
				String email = new DbAuthModule().getMail(null, userId);
				String firstName = new DbAuthModule().getName(null, userId);
				String externalUserType = getExternalUserType(userId);
				int becomeModeratorAsInt = (moderated) ? 1 : 0; // If is moderated set automatically moderated by
																// creator
				int showAudioVideoTestAsInt = 1; // By default show audio / video test
				UserService.setUserObjectAndGenerateRoomHashByURL(SID, userId, firstName, "", "", email, userId,
						externalUserType, roomId, becomeModeratorAsInt, showAudioVideoTestAsInt);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
	}
	
	@Override
	public List<GWTRoom> getPrivateConferenceUserRooms(String SID) throws OKMException {
		log.debug("getPrivateConferenceUserRooms({})", SID);
		updateSessionManager();
		List<GWTRoom> rooms = new ArrayList<GWTRoom>();
		
		try {
			String externalRoomType = getRoomType(GWTRoom.TYPE_CONFERENCE);
			String orderBy = "rooms_id";
			for (Room room : RoomService.getRoomsWithCurrentUsersByListAndType(SID, 0, Integer.MAX_VALUE, orderBy,
					true, externalRoomType)) {
				if (!room.isPub()) { // Only copy not public rooms
					rooms.add(OpenMeetingsUtils.copy(room));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		
		return rooms;
	}
	
	@Override
	public List<GWTRoom> getRestrictedUserRooms(String SID) throws OKMException {
		log.debug("getRestrictedUserRooms({})", SID);
		updateSessionManager();
		List<GWTRoom> rooms = new ArrayList<GWTRoom>();
		
		try {
			String externalRoomType = getRoomType(GWTRoom.TYPE_RESTRICTED);
			String orderBy = "rooms_id";
			for (Room room : RoomService.getRoomsWithCurrentUsersByListAndType(SID, 0, Integer.MAX_VALUE, orderBy,
					true, externalRoomType)) {
				rooms.add(OpenMeetingsUtils.copy(room));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		
		return rooms;
	}
	
	@Override
	public List<GWTRoom> getInterviewUserRooms(String SID) throws OKMException {
		log.debug("getInterviewUserRooms({})", SID);
		updateSessionManager();
		List<GWTRoom> rooms = new ArrayList<GWTRoom>();
		
		try {
			String externalRoomType = getRoomType(GWTRoom.TYPE_INTERVIEW);
			String orderBy = "rooms_id";
			for (Room room : RoomService.getRoomsWithCurrentUsersByListAndType(SID, 0, Integer.MAX_VALUE, orderBy,
					true, externalRoomType)) {
				rooms.add(OpenMeetingsUtils.copy(room));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		
		return rooms;
	}
	
	@Override
	public void deleteRoom(String SID, long rooms_id) throws OKMException {
		log.debug("getInterviewUserRooms({},{})", SID, rooms_id);
		updateSessionManager();
		try {
			RoomService.deleteRoom(SID, rooms_id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
	}
	
	@Override
	public void sendInvitation(String SID, long roomId, String users, String roles, String subject, String message,
			String lang) throws OKMException {
		try {
			List<String> userNames = new ArrayList<String>(Arrays.asList(users.isEmpty() ? new String[0] : users.split(",")));
			List<String> roleNames = new ArrayList<String>(Arrays.asList(roles.isEmpty() ? new String[0] : roles.split(",")));
			
			for (String role : roleNames) {
				List<String> usersInRole;
				
				usersInRole = OKMAuth.getInstance().getUsersByRole(null, role);
				
				for (String user : usersInRole) {
					if (!userNames.contains(user)) {
						userNames.add(user);
					}
				}
			}
			
			String baseUrl = OpenMeetingsUtils.getOpenMeetingServerURL();
			String conferencedomain = "";
			Boolean isPasswordProtected = new Boolean(false);
			Long languageId = new Long(OpenMeetingsUtils.getOpenMeetingsLang(lang));
			String invitationpass = "";
			Integer valid = new Integer(1); // the type of validation for the hash 1: endless, 2: from-to period, 3:
											// one-time
			String validFromDate = "", validFromTime = "", validToDate = "", validToTime = "";
			
			Boolean sendMail = new Boolean(true);
			for (String userId : userNames) {
				String email = new DbAuthModule().getMail(null, userId);
				String firstName = new DbAuthModule().getName(null, userId);
				int becomeModeratorAsInt = 0; // Invited are not moderators
				int showAudioVideoTestAsInt = 1; // By default show audio / video test
				// Creates user credentials
				UserService.setUserObjectAndGenerateRoomHashByURL(SID, userId, firstName, "", "", email, userId, "",
						roomId, becomeModeratorAsInt, showAudioVideoTestAsInt);
				
				// Invitation
				RoomService.sendInvitationHash(SID, userId, message, baseUrl, email, subject, roomId, conferencedomain,
						isPasswordProtected, invitationpass, valid, validFromDate, validFromTime, validToDate,
						validToTime, languageId, sendMail);
			}
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
	}
	
	@Override
	public void addDocumentToRoom(String SID, long roomId, String path) throws OKMException {
		String userId = getThreadLocalRequest().getRemoteUser();
		Boolean isOwner = new Boolean(true);
		long parentFolderId = 0; 
		if (isOwner.booleanValue()) {
			parentFolderId = -2; // 0 user is not the owner, -2 user is owner
		}
		String externalType = getExternalType();
		try {
			FileService.importFileByInternalUserId(SID, userId, externalFileId, externalType, roomId, isOwner,
					"http://localhost/grecia.txt", parentFolderId, "grecia.txt");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(
					ErrorCode.get(ErrorCode.ORIGIN_OKMOpenMeetingsService, ErrorCode.CAUSE_OpenMeetings),
					e.getMessage());
		}
		externalFileId++; // Incrementing file id
	}
	
	/**
	 * getRoomType
	 */
	private String getRoomType(long roomType) {
		return "OPENKM_" + String.valueOf(roomType) + "_" + getThreadLocalRequest().getRemoteUser();
	}
	
	/**
	 * getExternalUserType
	 */
	private String getExternalUserType(String userId) {
		return "OPENKM_USER_" + userId;
	}
	
	/**
	 * getExternalType
	 */
	private String getExternalType() {
		return "OPENKM";
	}
}