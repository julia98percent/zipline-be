package com.zipline.global.exception.custom.label;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.BaseException;

public class LabelNameDuplicatedException extends BaseException {

	public LabelNameDuplicatedException(String message, HttpStatus status) {
		super(message, status);
	}
}