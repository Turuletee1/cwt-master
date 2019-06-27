package com.ibm.cwt.dto;

public class ReportRowDTO {
	private String cc;
	private String dept;
	private String serial;
	private String status;
	private String name;
	private String week;
	private String email;
	private String managerEmail;
	
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getManagerEmail() {
		return managerEmail;
	}
	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}
	
	@Override
	public String toString() {
		return "ReportRowDTO [cc=" + cc + ", dept=" + dept + ", serial=" + serial + ", status=" + status + ", name="
				+ name + ", week=" + week + ", email=" + email + "]";
	}
	
	public String getUID() {
		return getSerial()+getCc();		
	}
	
	
}
