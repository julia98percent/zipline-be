package com.zipline.global.exception.custom.customer;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class PriceValidationException extends BaseException {
	public PriceValidationException(String message, HttpStatus status) {
		super(message, status);
	}
}
