package com.cloudsearch.model;

public class ResponseModel {
	private int code;
	private String model;

	public ResponseModel(int code, String model) {
		this.code = code;
		this.model = model;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}
