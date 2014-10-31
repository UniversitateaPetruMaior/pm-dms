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
import java.util.HashSet;
import java.util.Set;

public class MailAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String PROTOCOL_POP3 = "pop3";
	public static final String PROTOCOL_POP3S = "pop3s";
	public static final String PROTOCOL_IMAP = "imap";
	public static final String PROTOCOL_IMAPS = "imaps";
	
	private long id = -1;
	private String user = "";
	private String mailProtocol = PROTOCOL_IMAP;
	private String mailHost = "";
	private String mailFolder = "";
	private String mailUser = "";
	private String mailPassword = "";
	private boolean mailMarkSeen = true;
	private boolean mailMarkDeleted = false;
	private long mailLastUid = 0;
	private Set<MailFilter> mailFilters = new HashSet<MailFilter>();
	private boolean active = false;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getMailProtocol() {
		return mailProtocol;
	}

	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol= mailProtocol;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailFolder() {
		return mailFolder;
	}

	public void setMailFolder(String mailFolder) {
		this.mailFolder = mailFolder;
	}
	
	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}
	
	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	
	public boolean isMailMarkSeen() {
		return mailMarkSeen;
	}

	public void setMailMarkSeen(boolean mailMarkSeen) {
		this.mailMarkSeen = mailMarkSeen;
	}

	public boolean isMailMarkDeleted() {
		return mailMarkDeleted;
	}

	public void setMailMarkDeleted(boolean mailMarkDeleted) {
		this.mailMarkDeleted = mailMarkDeleted;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<MailFilter> getMailFilters() {
		return mailFilters;
	}

	public void setMailFilters(Set<MailFilter> mailFilters) {
		this.mailFilters = mailFilters;
	}
	
	public long getMailLastUid() {
		return mailLastUid;
	}

	public void setMailLastUid(long mailLastUid) {
		this.mailLastUid = mailLastUid;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", user="); sb.append(user);
		sb.append(", mailProtocol="); sb.append(mailProtocol);
		sb.append(", mailHost="); sb.append(mailHost);
		sb.append(", mailFolder="); sb.append(mailFolder);
		sb.append(", mailUser="); sb.append(mailUser);
		sb.append(", mailPassword="); sb.append(mailPassword);
		sb.append(", mailMarkSeen="); sb.append(mailMarkSeen);
		sb.append(", mailMarkDeleted="); sb.append(mailMarkDeleted);
		sb.append(", mailLastUid="); sb.append(mailLastUid);
		sb.append(", active="); sb.append(active);
		sb.append(", mailFilters="); sb.append(mailFilters);
		sb.append("}");
		return sb.toString();
	}
}
