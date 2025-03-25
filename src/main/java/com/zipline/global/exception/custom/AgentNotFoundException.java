package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class AgentNotFoundException extends BaseException {
	public AgentNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}

}
