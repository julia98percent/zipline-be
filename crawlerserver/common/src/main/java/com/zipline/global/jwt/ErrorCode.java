package com.zipline.global.jwt;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	UNAUTHORIZED_CLIENT(HttpStatus.BAD_REQUEST, "접근 토큰이 없습니다."),
	FORBIDDEN_CLIENT(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
	JWT_DECODE_FAIL(HttpStatus.UNAUTHORIZED, "올바른 토큰이 필요합니다."),
	MISSING_AUTHORITY(HttpStatus.UNAUTHORIZED, "토큰에 권한 정보가 없습니다."),
	JWT_SIGNATURE_FAIL(HttpStatus.UNAUTHORIZED, "올바른 토큰이 필요합니다.");

	private HttpStatus httpStatus;
	private String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
