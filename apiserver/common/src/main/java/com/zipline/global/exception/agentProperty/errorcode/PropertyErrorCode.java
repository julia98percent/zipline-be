package com.zipline.global.exception.agentProperty.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum PropertyErrorCode implements ErrorCode {
	PROPERTY_NOT_FOUND("PROPERTY-001", "해당하는 매물을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PROPERTY_TYPE_NOT_FOUND("PROPERTY-002", "존재하지 않는 매물 타입입니다.", HttpStatus.NOT_FOUND),
	PROPERTY_CATEGORY_NOT_FOUND("PROPERTY-003", "존재하지 않는 매물 카테고리입니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus status;

	PropertyErrorCode(String code, String message, HttpStatus status) {
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
