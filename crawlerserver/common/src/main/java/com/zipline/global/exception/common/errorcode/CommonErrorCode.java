package com.zipline.global.exception.common.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
	INTERNAL_SERVER_ERROR("COMMON-000", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_INPUT_VALUE("COMMON-001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
	METHOD_NOT_ALLOWED("COMMON-002", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
	FILE_TOO_LARGE("COMMON-003", "파일의 크기가 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE);

	private final String code;
	private final String message;
	private final HttpStatus status;

	CommonErrorCode(String code, String message, HttpStatus status) {
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
