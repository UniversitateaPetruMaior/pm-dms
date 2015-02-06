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

import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.openmeetings.OpenMeetingsUtils;

/**
 * UserService
 * 
 * @author jllort
 * 
 */
public class UserService {
	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	/**
	 * loginUser
	 * 
	 */
	public static String loginUser() throws Exception {
		String SID = null;
		Map<String, Element> result = null;
		String sessionURL = OpenMeetingsUtils.getOpenMeetingServerURL() + "/services/UserService/getSession";
		Map<String, Element> elementMap = RestService.callMap(sessionURL, null);
		Element item = (Element) elementMap.get("return");
		SID = item.elementText("session_id");
		log.info(SID);
		
		result = RestService.callMap(OpenMeetingsUtils.getOpenMeetingServerURL()
				+ "/services/UserService/loginUser?SID=" + SID + "&username=" + Config.OPENMEETINGS_USER + "&userpass="
				+ Config.OPENMEETINGS_CREDENTIALS, null);
		
		if (Integer.valueOf(((Element) result.get("return")).getStringValue()).intValue() > 0) {
			return SID;
		}
		return null;
	}
	
	/**
	 * setUserObjectAndGenerateRoomHash
	 */
	public static String setUserObjectAndGenerateRoomHash(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email, String externalUserId, String externalUserType,
			long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt) throws Exception {
		String roomHash = null;
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL()
				+ "/services/UserService/setUserObjectAndGenerateRoomHash?" + "SID=" + SID + "&username=" + username
				+ "&firstname=" + firstname + "&lastname=" + lastname + "&profilePictureUrl=" + profilePictureUrl
				+ "&email=" + email + "&externalUserId=" + externalUserId + "&externalUserType=" + externalUserType
				+ "&room_id=" + String.valueOf(room_id) + "&becomeModeratorAsInt=" + becomeModeratorAsInt
				+ "&showAudioVideoTestAsInt=" + showAudioVideoTestAsInt;
		
		Map<String, Element> result = RestService.callMap(restURL, null);
		roomHash = ((Element) result.get("return")).getStringValue();
		return roomHash;
	}
	
	/**
	 * setUserObjectAndGenerateRoomHashByURL
	 */
	public static String setUserObjectAndGenerateRoomHashByURL(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email, String externalUserId, String externalUserType,
			long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt) throws Exception {
		String roomHash = null;
		String restURL = OpenMeetingsUtils.getOpenMeetingServerURL()
				+ "/services/UserService/setUserObjectAndGenerateRoomHashByURL?" + "SID=" + SID + "&username="
				+ username + "&firstname=" + firstname + "&lastname=" + lastname + "&profilePictureUrl="
				+ profilePictureUrl + "&email=" + email + "&externalUserId=" + externalUserId + "&externalUserType="
				+ externalUserType + "&room_id=" + String.valueOf(room_id) + "&becomeModeratorAsInt="
				+ becomeModeratorAsInt + "&showAudioVideoTestAsInt=" + showAudioVideoTestAsInt;
		
		Map<String, Element> result = RestService.callMap(restURL, null);
		roomHash = ((Element) result.get("return")).getStringValue();
		return roomHash;
	}
	
	// /**
	// * setUserObjectAndGenerateRoomHashByURLAndRecFlag
	// */
	// public static String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID, String username, String
	// firstname,
	// String lastname, String profilePictureUrl, String email, String externalUserId, String externalUserType,
	// long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt) throws Exception {
	// String roomHash = null;
	// String restURL = "http://" + Config.OPENMEETINGS_URL + ":" + Config.OPENMEETINGS_PORT
	// + "/openmeetings/services/UserService/setUserObjectAndGenerateRoomHashByURLAndRecFlag?" + "SID=" + SID
	// + "&username=" + username + "&firstname=" + firstname + "&lastname=" + lastname + "&profilePictureUrl="
	// + profilePictureUrl + "&email=" + email + "&externalUserId=" + externalUserId + "&externalUserType="
	// + externalUserType + "&room_id=" + String.valueOf(room_id) + "&becomeModeratorAsInt="
	// + becomeModeratorAsInt + "&showAudioVideoTestAsInt=" + showAudioVideoTestAsInt;
	//
	// Map<String, Element> result = RestService.callMap(restURL, null);
	// roomHash = ((Element) result.get("return")).getStringValue();
	// return roomHash;
	// }
}