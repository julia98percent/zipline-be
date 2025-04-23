package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;


public class ChoiceNotAllowedException extends BaseException {
	public ChoiceNotAllowedException(String message, HttpStatus status) {
		super(message, status);
	}
}