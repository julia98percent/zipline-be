package com.zipline.global.exception.auth.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
	UNAUTHORIZED_CLIENT("AUTH-001", "인증 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
	FORBIDDEN_CLIENT("AUTH-002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	EXPIRED_TOKEN("AUTH-003", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	JWT_DECODE_FAIL("AUTH-004", "잘못된 형식의 토큰입니다.", HttpStatus.UNAUTHORIZED),
	JWT_SIGNATURE_FAIL("AUTH-005", "토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
	MISSING_AUTHORITY("AUTH-006", "토큰에 권한 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
	PERMISSION_DENIED("AUTH-007", "권한이 없습니다.", HttpStatus.FORBIDDEN);

	private final String code;
	private final String message;
	private final HttpStatus status;

	AuthErrorCode(String code, String message, HttpStatus status) {
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
