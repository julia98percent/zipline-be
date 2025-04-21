package com.zipline.global.exception.custom.contract;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class ContractNotFoundException extends BaseException {
	public ContractNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}
