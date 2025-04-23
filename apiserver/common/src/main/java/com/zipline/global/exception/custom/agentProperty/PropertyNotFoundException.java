package com.zipline.global.exception.custom.agentProperty;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PropertyNotFoundException extends BaseException {

	public PropertyNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}