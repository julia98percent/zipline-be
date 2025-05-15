package com.zipline.global.exception.customer.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum CustomerErrorCode implements ErrorCode {
	CUSTOMER_NOT_FOUND("CUSTOMER-001", "해당하는 고객을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	NEGATIVE_PRICE("CUSTOMER-002", "가격은 음수일 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PRICE_RANGE("CUSTOMER-003", "최소 가격이 최대 가격보다 클 수 없습니다.", HttpStatus.BAD_REQUEST),
	DUPLICATED_CUSTOMER("CUSTOMER-004", "중복된 고객이 존재합니다.", HttpStatus.CONFLICT);

	private final String code;
	private final String message;
	private final HttpStatus status;

	CustomerErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
}
