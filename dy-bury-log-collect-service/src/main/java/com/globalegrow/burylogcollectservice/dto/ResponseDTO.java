package com.globalegrow.burylogcollectservice.dto;

import java.io.Serializable;

public class ResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7904117316297257928L;
	public ResponseDTO() {
		super();
	}
	
	public ResponseDTO(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	private String code;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
