package com.zipline.global.exception.custom.user;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.BaseException;

public class UserNotFoundException extends BaseException {
	public UserNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}

}