package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class QuestionTypeException extends BaseException {
	public QuestionTypeException(String message, HttpStatus status) {
		super(message, status);
	}
}
