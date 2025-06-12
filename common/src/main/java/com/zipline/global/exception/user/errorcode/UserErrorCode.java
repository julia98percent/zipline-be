package com.zipline.global.exception.user.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND("USER-001", "해당하는 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_USER_ID("USER-002", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
	INVALID_PASSWORD_CHECK("USER-003", "비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
	INVALID_CREDENTIALS("USER-004", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
	PASSWORD_QUESTION_NOT_FOUND("USER-005", "해당하는 비밀번호 찾기 질문이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	INCORRECT_QUESTION_ANSWER("USER-006", "비밀번호 찾기 답변이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

	UserErrorCode(String code, String message, HttpStatus status) {
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
