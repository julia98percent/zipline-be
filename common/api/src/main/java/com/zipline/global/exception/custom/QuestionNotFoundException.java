package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends BaseException {
	public QuestionNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
