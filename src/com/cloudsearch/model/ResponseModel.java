package com.cloudsearch.model;

import org.apache.log4j.Logger;

import com.cloudsearch.abstractwebservices.CloudSearchService;

public class ResponseModel {
	static Logger log = Logger.getLogger(ResponseModel.class);

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
