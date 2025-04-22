package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends BaseException {

	public PermissionDeniedException(String message, HttpStatus status) {
		super(message, status);
	}
}
