package com.openkm.dao.bean;

import java.io.Serializable;
import java.util.Calendar;

public class UserCertificate implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id = 0;
	private String user = "";
	private String certData;
	private String certHash;
	private Calendar startDate;
	private Calendar endDate;
	private String serialNr;
    private String subjectDn;
    private String issuerDn;
	private boolean valid = false;
	
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCertData() {
		return certData;
	}

	public void setCertData(String certData) {
		this.certData = certData;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public String getSerialNr() {
		return serialNr;
	}

	public void setSerialNr(String serialNr) {
		this.serialNr = serialNr;
	}

	public String getSubjectDn() {
		return subjectDn;
	}

	public void setSubjectDn(String subjectDn) {
		this.subjectDn = subjectDn;
	}

	public String getIssuerDn() {
		return issuerDn;
	}

	public void setIssuerDn(String issuerDn) {
		this.issuerDn = issuerDn;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getCertHash() {
		return certHash;
	}

	public void setCertHash(String certHash) {
		this.certHash = certHash;
	}
	
}
