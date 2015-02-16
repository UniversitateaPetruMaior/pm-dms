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

package com.openkm.dao.bean;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "OKM_NODE_SIGNATURE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NodeSignature implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "NSG_UUID", length = 64)
	private String uuid;
	
	@Column(name = "NSG_PARENT", length = 64)
	private String parent;
	
	@Column(name = "NSG_USER", length = 64)
	private String user;
	
	@Column(name = "NSG_CREATED")
	protected Calendar created;
	
	@Column(name = "NSG_CONTENT")
	@Lob @Type(type = "org.hibernate.type.StringClobType")
	private String signContent;

	@Column(name = "NSG_DIGEST", length = 255)
	private String signDigest;
	
	@Column(name = "NSG_SHA1", length = 255)
	private String signSHA1;
	
	@Column(name = "NSG_SIZE")
	private long signSize;

	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String author) {
		this.user = author;
	}
	
	public Calendar getCreated() {
		return created;
	}
	
	public void setCreated(Calendar created) {
		this.created = created;
	}	
	
	public String getSignContent() {
		return signContent;
	}

	public void setSignContent(String signContent) {
		this.signContent = signContent;
	}

	public String getSignDigest() {
		return signDigest;
	}

	public void setSignDigest(String signDigest) {
		this.signDigest = signDigest;
	}

	public String getSignSHA1() {
		return signSHA1;
	}

	public void setSignSHA1(String signSHA1) {
		this.signSHA1 = signSHA1;
	}

	public long getSignSize() {
		return signSize;
	}

	public void setSignSize(long signSize) {
		this.signSize = signSize;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid=").append(uuid);
		sb.append(", parent=").append(parent);
		sb.append(", user=").append(user);
		sb.append(", created=").append(created == null ? null : created.getTime());
		sb.append(", signContent=").append(signContent==null?null:signContent.length()>10 ? signContent.substring(0, 10) + "...[" + signContent.length() + "]" : signContent);
		sb.append(", signDigest=").append(signDigest);
		sb.append(", signSHA1=").append(signSHA1);
		sb.append(", signSize=").append(signSize);
		sb.append("}");
		return sb.toString();
	}
}
