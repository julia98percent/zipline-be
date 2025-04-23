package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
	public UserNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}

}