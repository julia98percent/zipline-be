package com.zipline.global.exception.common.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum CommonErrorCode implements ErrorCode {
	INVALID_INPUT_VALUE("COMMON-001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
	METHOD_NOT_ALLOWED("COMMON-002", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
	FILE_TOO_LARGE("COMMON-003", "파일의 크기가 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE),
	INTERNAL_SERVER_ERROR("COMMON-004", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_TYPE_NOT_SUPPORTED("COMMON-005", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
	FILE_VALIDATION_FAILED("COMMON-006", "파일 검증에 실패하였습니다.", HttpStatus.BAD_REQUEST),
	FILE_UPLOAD_FAILED("COMMON-007", "파일 업로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
