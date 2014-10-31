package com.openkm.frontend.client.bean;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTSignature implements IsSerializable {

	private Date date;
	private String user;
	private String path;
	private String sha1;
	private boolean valid;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
	
	
}
