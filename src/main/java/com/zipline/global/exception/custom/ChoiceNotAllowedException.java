package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class ChoiceNotAllowedException extends BaseException {
	public ChoiceNotAllowedException(String message, HttpStatus status) {
		super(message, status);
	}
}
