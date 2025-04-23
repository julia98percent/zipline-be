package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuestionTypeException extends BaseException {
	public QuestionTypeException(String message, HttpStatus status) {
		super(message, status);
	}
}