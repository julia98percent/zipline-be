package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends BaseException {
	public QuestionNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}