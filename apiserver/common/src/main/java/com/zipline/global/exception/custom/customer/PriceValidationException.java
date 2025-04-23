package com.zipline.global.exception.custom.customer;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PriceValidationException extends BaseException {
	public PriceValidationException(String message, HttpStatus status) {
		super(message, status);
	}
}