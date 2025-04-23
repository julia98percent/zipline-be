package com.zipline.global.exception.custom.contract;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;


public class ContractNotFoundException extends BaseException {
	public ContractNotFoundException(String message, HttpStatus status) {
		super(message, status);
	}
}