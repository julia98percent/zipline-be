package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SurveyNotFoundException extends BaseException {
	public SurveyNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}