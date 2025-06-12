package com.zipline.global.exception.excel.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum ExcelErrorCode implements ErrorCode {
	INVALID_INPUT_VALUE("EXCEL-001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
	INVALID_PREFERRED_REGION("EXCEL-002", "유효하지 않은 희망 지역 입력입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATED_CUSTOMER("EXCEL-002", "중복된 고객이 존재합니다.", HttpStatus.BAD_REQUEST),
	;

	private final String code;
	private final String message;
	private final HttpStatus status;

	ExcelErrorCode(String code, String message, HttpStatus status) {
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
