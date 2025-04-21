package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class SurveyNotFoundException extends BaseException {
	public SurveyNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
