package com.zipline.global.exception.counsel.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum CounselErrorCode implements ErrorCode {
	COUNSEL_NOT_FOUND("COUNSEL-001", "해당하는 상담을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_COUNSEL_TYPE("COUNSEL-002", "잘못된 상담 종류입니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

	CounselErrorCode(String code, String message, HttpStatus status) {
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
