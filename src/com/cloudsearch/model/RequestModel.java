package com.cloudsearch.model;

import java.util.Map;

public class RequestModel {
	private String state;
	private String userId;
	private String code;
	private String email;
	private Map<String, String> parameter;
	public RequestModel(String state, String userId, String code, String email) {
		this.code = code;
		this.email = email;
		this.state = state;
		this.userId = userId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, String> getParameter() {
		return parameter;
	}

	public void setParameter(Map<String, String> parameter) {
		this.parameter = parameter;
	}
	
	public String getParameter(String key) {
		return parameter.get(key);
	}

}
