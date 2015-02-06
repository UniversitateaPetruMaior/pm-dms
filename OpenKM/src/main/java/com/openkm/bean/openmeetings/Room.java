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
package com.openkm.bean.openmeetings;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Element;

/**
 * Room
 * 
 * @author jllort
 * 
 */
public class Room implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_CONFERENCE = 1;
	public static final int TYPE_AUDIENCE = 2;
	
	private int id = 0;
	private String name;
	private int type = 0; // Room type
	private boolean pub = false; // Is Public
	private Date start;
	
	/**
	 * Room
	 * 
	 * @throws ParseException
	 */
	public Room(Element element) throws ParseException {
		if (element.elementText("name") != null) {
			name = element.elementText("name");
		}
		if (element.elementText("rooms_id") != null) { // Depending the xml result comes with room_id or rooms_id
			id = Integer.parseInt(element.elementText("rooms_id"));
		} else if (element.elementText("room_id") != null) {
			id = Integer.parseInt(element.elementText("room_id"));
		}
		if (element.element("roomtype") != null && element.element("roomtype").elementText("roomtypes_id") != null) {
			type = Integer.parseInt(element.element("roomtype").elementText("roomtypes_id"));
		}
		if (element.elementText("ispublic") != null) {
			pub = Boolean.getBoolean(element.elementText("ispublic"));
		}
		if (element.elementText("starttime") != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(element.elementText("starttime"));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isPub() {
		return pub;
	}
	
	public void setPub(boolean pub) {
		this.pub = pub;
	}
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
}
