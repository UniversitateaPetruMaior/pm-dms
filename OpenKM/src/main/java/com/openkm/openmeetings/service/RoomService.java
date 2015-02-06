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
package com.openkm.openmeetings.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.openmeetings.Room;
import com.openkm.openmeetings.OpenMeetingsUtils;

/**
 * RoomService
 * 
 * @author jllort
 * 
 */
public class RoomService {
	private static Logger log = LoggerFactory.getLogger(RoomService.class);
	
	/**
	 * getRoomsPublic
	 */
	public static List<Room> getRoomsPublic(String SID, long roomType) throws Exception {
		log.debug("getRoomsPublic({},{})", SID, roomType);
		List<Room> rooms = new ArrayList<Room>();
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL() + "/services/RoomService/getRoomsPublic?" + "SID="
				+ SID + "&roomtypes_id=" + String.valueOf(roomType);
		List<Element> result = RestService.callList(restURL, null);
		for (Element element : result) {
			if (element.elements().size() > 0) { // Evaluate null case ( has no elements inside )
				Room room = new Room(element);
				rooms.add(room);
			}
		}
		return rooms;
	}
	
	/**
	 * addRoomWithModerationExternalTypeAndTopBarOption
	 */
	public static long addRoomWithModerationExternalTypeAndTopBarOption(String SID, String roomName, long roomType,
			String comment, long numberOfPartizipants, Boolean isPublic, Boolean appointment, Boolean demoRoom,
			Boolean isModeratedRoom, String externalRoomType, Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean waitForRecording, Boolean isAllowedRecording, Boolean hideTopBar) throws Exception {
		String roomId = "";
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL()
				+ "/services/RoomService/addRoomWithModerationExternalTypeAndTopBarOption?" + "SID=" + SID + "&name="
				+ roomName + "&roomtypes_id=" + String.valueOf(roomType) + "&comment=" + comment
				+ "&numberOfPartizipants=" + String.valueOf(numberOfPartizipants) + "&ispublic=" + isPublic.toString()
				+ "&appointment=" + appointment.toString() + "&isDemoRoom=" + demoRoom.toString() + "&demoTime="
				+ "&isModeratedRoom=" + isModeratedRoom.toString() + "&externalRoomType=" + externalRoomType
				+ "&allowUserQuestions=" + allowUserQuestions.toString() + "&isAudioOnly=" + isAudioOnly.toString()
				+ "&waitForRecording=" + waitForRecording.toString() + "&allowRecording="
				+ isAllowedRecording.toString() + "&hideTopBar=" + hideTopBar.toString();
		Map<String, Element> result = RestService.callMap(restURL, null);
		roomId = ((Element) result.get("return")).getStringValue();
		return Long.valueOf(roomId).longValue();
	}
	
	/**
	 * getRoomsWithCurrentUsersByListAndType
	 */
	public static List<Room> getRoomsWithCurrentUsersByListAndType(String SID, int start, int max, String orderBy,
			Boolean asc, String externalRoomType) throws Exception {
		List<Room> rooms = new ArrayList<Room>();
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL()
				+ "/services/RoomService/getRoomsWithCurrentUsersByListAndType?" + "SID=" + SID + "&start="
				+ String.valueOf(start) + "&max=" + String.valueOf(max) + "&orderby=" + orderBy + "&asc="
				+ asc.toString() + "&externalRoomType=" + externalRoomType;
		List<Element> result = RestService.callList(restURL, null);
		for (Element element : result) {
			if (element.elements().size() > 0) { // Evaluate null case ( has no elements inside )
				Room room = new Room(element); // only returns room_id need extra call to get complete room information
				room = getRoomById(SID, room.getId());
				rooms.add(room);
			}
		}
		return rooms;
	}
	
	/**
	 * getRoomById
	 */
	public static Room getRoomById(String SID, long rooms_id) throws Exception {
		Room room = null;
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL() + "/services/RoomService/getRoomById?" + "SID="
				+ SID + "&rooms_id=" + String.valueOf(rooms_id);
		Map<String, Element> result = RestService.callMap(restURL, null);
		room = new Room(((Element) result.get("return")));
		return room;
	}
	
	/**
	 * deleteRoom
	 */
	public static long deleteRoom(String SID, long rooms_id) throws Exception {
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL() + "/services/RoomService/deleteRoom?" + "SID="
				+ SID + "&rooms_id=" + String.valueOf(rooms_id);
		Map<String, Element> result = RestService.callMap(restURL, null);
		String value = ((Element) result.get("return")).getStringValue();
		return Long.valueOf(value).longValue();
	}
	
	/**
	 * sendInvitationHash
	 * 
	 * @throws Exception
	 */
	public static String sendInvitationHash(String SID, String username, String message, String baseurl, String email,
			String subject, Long room_id, String conferencedomain, Boolean isPasswordProtected, String invitationpass,
			Integer valid, String validFromDate, String validFromTime, String validToDate, String validToTime,
			Long languageId, Boolean sendMail) throws Exception {
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL() + "/services/RoomService/sendInvitationHash?"
				+ "SID=" + SID + "&username=" + username + "&message=" + message + "&baseurl=" + baseurl + "&email="
				+ email + "&subject=" + subject + "&room_id=" + room_id + "&conferencedomain=" + conferencedomain
				+ "&isPasswordProtected=" + isPasswordProtected.toString() + "&invitationpass=" + invitationpass
				+ "&valid=" + valid.toString() + "&validFromDate=" + validFromDate + "&validFromTime=" + validFromTime
				+ "&validToDate=" + validToDate + "&validToTime=" + validToTime + "&language_id="
				+ languageId.toString() + "&sendMail=" + sendMail.toString();
		Map<String, Element> result = RestService.callMap(restURL, null);
		return ((Element) result.get("return")).getStringValue();
	}
	
	// pe?SID=3265677c3ff1d17c66529e69c71362e8&start=0&max=60&orderby=rooms_id&asc=true&externalRoomType=OPENKM
	
	// /**
	// * getInvitationHash
	// */
	// public static String getInvitationHash(String SID, String username, long room_id, boolean isPasswordProtected,
	// String invitationpass, int valid, String validFromDate, String validFromTime, String validToDate,
	// String validToTime) throws Exception {
	// String invitationHash = null;
	// String restURL = "http://" + Config.OPENMEETINGS_URL + ":" + Config.OPENMEETINGS_PORT
	// + "/openmeetings/services/RoomService/getInvitationHash?" + "SID=" + SID + "&username=" + username
	// + "&room_id=" + String.valueOf(room_id) + "&isPasswordProtected=" + String.valueOf(isPasswordProtected)
	// + "&invitationpass=" + invitationpass + "&valid=" + String.valueOf(valid) + "&validFromDate="
	// + validFromDate + "&validFromTime=" + validFromTime + "&validToDate=" + validToDate + "&validToTime="
	// + validToTime;
	// Map<String, Element> result = RestService.callMap(restURL, null);
	// invitationHash = ((Element) result.get("return")).getStringValue();
	// return invitationHash;
	// }
	
	// /**
	// * updateRoomWithModerationAndQuestions
	// */
	// public long updateRoomWithModerationAndQuestions(String SID, Boolean isAllowedRecording, Boolean isAudioOnly,
	// Boolean isModeratedRoom, String roomname, long numberOfParticipent, long roomType, long roomId)
	// throws Exception {
	// String updateRoomId = "";
	// String restURL = "http://" + Config.OPENMEETINGS_URL + ":" + Config.OPENMEETINGS_PORT
	// + "/openmeetings/services/RoomService/updateRoomWithModerationAndQuestions?" + "SID=" + SID
	// + "&room_id=" + String.valueOf(roomId) + "&name=" + roomname.toString() + "&roomtypes_id="
	// + String.valueOf(roomType) + "&comment=" + "&numberOfPartizipants="
	// + String.valueOf(numberOfParticipent) + "&ispublic=false" + "&appointment=false" + "&isDemoRoom=false"
	// + "&demoTime=" + "&isModeratedRoom=" + isModeratedRoom.toString() + "&allowUserQuestions=";
	//
	// Map<String, Element> result = RestService.callMap(restURL, null);
	// log.info("addRoomWithModerationExternalTypeAndTopBarOption with ID: ",
	// ((Element) result.get("return")).getStringValue());
	// updateRoomId = ((Element) result.get("return")).getStringValue();
	// return Long.valueOf(updateRoomId).longValue();
	// }
	
}