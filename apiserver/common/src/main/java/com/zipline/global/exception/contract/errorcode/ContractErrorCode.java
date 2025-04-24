package com.zipline.global.exception.contract.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum ContractErrorCode implements ErrorCode {
	CONTRACT_NOT_FOUND("CONTRACT-001", "해당하는 계약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus status;

	ContractErrorCode(String code, String message, HttpStatus status) {
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
