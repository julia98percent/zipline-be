package com.zipline.global.exception.external.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExternalErrorCode implements ErrorCode {
	KAKAO_CLIENT_ERROR("EXTERNAL_001", "카카오 API 클라이언트 오류", HttpStatus.BAD_REQUEST),
	KAKAO_INVALID_API_KEY("EXTERNAL_002", "카카오 API 인증 실패", HttpStatus.UNAUTHORIZED),
	KAKAO_SERVER_ERROR("EXTERNAL_003", "카카오 API 서버 오류", HttpStatus.INTERNAL_SERVER_ERROR),
	KAKAO_CONNECTION_FAIL("EXTERNAL_004", "카카오 API 통신 실패", HttpStatus.BAD_GATEWAY),
	KAKAO_UNKNOWN_ERROR("EXTERNAL_005", "카카오 API 호출 중 알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;

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
