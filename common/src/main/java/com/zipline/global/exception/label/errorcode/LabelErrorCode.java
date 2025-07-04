package com.zipline.global.exception.label.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum LabelErrorCode implements ErrorCode {
	LABEL_DUPLICATE("LABEL-001", "이미 존재하는 라벨입니다.", HttpStatus.CONFLICT),
	LABEL_NOT_FOUND("LABEL-002", "해당 라벨을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus status;

	LabelErrorCode(String code, String message, HttpStatus status) {
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
