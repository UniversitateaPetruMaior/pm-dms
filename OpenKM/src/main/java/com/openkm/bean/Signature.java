/**
 *
 */

package com.openkm.bean;

import java.io.Serializable;
import java.util.Calendar;

public class Signature implements Serializable {
	
	private static final long serialVersionUID = 913105621252127904L;
	
	public static final String TYPE = "okm:sign";
	public static final String LIST = "okm:signs";
	public static final String LIST_TYPE = "okm:signs";
	public static final String MIX_TYPE = "mix:signs";
	
	public static final String DATE = "okm:date";
	public static final String USER = "okm:user";
	public static final String SIGN_SHA1 = "okm:signSHA1";
	public static final String SIGN_DIGEST = "okm:signDigest";
	public static final String SIGN_CONTENT = "okm:signContent";
	public static final String SIGN_SIZE = "okm:size";
	
	private Calendar date;
	private String user;
	private String signContent;
	private String signDigest;
	private String signSHA1;
	private long signSize;
		
	private String path;
	private boolean valid;

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSignContent() {
		return signContent;
	}

	public void setSignContent(String signContent) {
		this.signContent = signContent;
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

	public String getSignDigest() {
		return signDigest;
	}

	public void setSignDigest(String signDigest) {
		this.signDigest = signDigest;
	}
	
	
	
}
