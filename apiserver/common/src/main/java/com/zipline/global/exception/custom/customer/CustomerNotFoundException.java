package com.zipline.global.exception.custom.customer;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends BaseException {

	public CustomerNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}