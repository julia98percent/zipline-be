package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class CounselNotFoundException extends BaseException {
	public CounselNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
