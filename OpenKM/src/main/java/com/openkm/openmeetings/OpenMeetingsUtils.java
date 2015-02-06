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
package com.openkm.openmeetings;

import com.openkm.bean.openmeetings.Room;
import com.openkm.core.Config;
import com.openkm.frontend.client.bean.extension.GWTRoom;

/**
 * OpenMeetingsUtils
 * 
 * @author jllort
 * 
 */
public class OpenMeetingsUtils {
	
	/**
	 * GWTRoom
	 * 
	 * @param room
	 * @return
	 */
	public static GWTRoom copy(Room room) {
		GWTRoom gWTRoom = new GWTRoom();
		gWTRoom.setId(room.getId());
		gWTRoom.setName(room.getName());
		gWTRoom.setPub(room.isPub());
		gWTRoom.setType(room.getType());
		gWTRoom.setStart(room.getStart());
		return gWTRoom;
	}
	
	/**
	 * getOpenMeetingServerURL
	 */
	public static String getOpenMeetingServerURL() {
		if (!Config.OPENMEETINGS_URL.startsWith("http")) {
			return "http://" + Config.OPENMEETINGS_URL + ":" + Config.OPENMEETINGS_PORT + "/openmeetings";
		} else {
			return Config.OPENMEETINGS_URL + ":" + Config.OPENMEETINGS_PORT + "/openmeetings";
		}
	}
	
	/**
	 * getOpenMeetingsLang
	 * 
	 * @param lang
	 * @return
	 */
	public static int getOpenMeetingsLang(String lang) {
		int language = 1; // By default english
		// 3 deutsch (studIP)
		// 13 korean
		// 19 ukranian
		// 21 persian
		// 24 finish
		// 28 hebrew
		if (lang.startsWith("en")) {
			language = 1;
		} else if (lang.equals("de-DE")) {
			language = 2;
		} else if (lang.startsWith("fr")) {
			language = 4;
		} else if (lang.startsWith("it")) {
			language = 5;
		} else if (lang.equals("pt-BR")) {
			language = 7;
		} else if (lang.startsWith("pt")) {
			language = 6;
		} else if (lang.startsWith("es")) {
			language = 8;
		} else if (lang.startsWith("ru")) {
			language = 9;
		} else if (lang.startsWith("sv")) {
			language = 10;
		} else if (lang.equals("zh-CN")) {
			language = 11;
		} else if (lang.equals("zh-TW")) {
			language = 12;
		} else if (lang.startsWith("ar")) {
			language = 14;
		} else if (lang.startsWith("jp")) {
			language = 15;
		} else if (lang.startsWith("id")) {
			language = 16;
		} else if (lang.startsWith("hu")) {
			language = 17;
		} else if (lang.startsWith("tr")) {
			language = 18;
		} else if (lang.startsWith("th")) {
			language = 20;
		} else if (lang.startsWith("tr")) {
			language = 18;
		} else if (lang.startsWith("cs")) {
			language = 22;
		} else if (lang.startsWith("gl")) {
			language = 23;
		} else if (lang.startsWith("pl")) {
			language = 25;
		} else if (lang.startsWith("el")) {
			language = 26;
		} else if (lang.startsWith("nl")) {
			language = 27;
		} else if (lang.startsWith("ca")) {
			language = 29;
		} else if (lang.startsWith("bg")) {
			language = 30;
		} else if (lang.startsWith("da")) {
			language = 31;
		} else if (lang.startsWith("sk")) {
			language = 27;
		}
		return language;
	}
}