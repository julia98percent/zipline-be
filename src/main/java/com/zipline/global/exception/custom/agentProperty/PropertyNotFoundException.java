package com.zipline.global.exception.custom.agentProperty;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class PropertyNotFoundException extends BaseException {

	public PropertyNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}