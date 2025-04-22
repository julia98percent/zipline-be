package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;
import com.zipline.global.exception.custom.BaseException;

public class ChoiceNotAllowedException extends BaseException{
	public ChoiceNotAllowedException(String message, HttpStatus status) {
		super(message, status);
	}
}