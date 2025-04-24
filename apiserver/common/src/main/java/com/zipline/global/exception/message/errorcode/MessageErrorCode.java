package com.zipline.global.exception.message.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum MessageErrorCode implements ErrorCode {
	MESSAGE_SEND_FAILED("MSG-001", "메시지 전송에 실패하였습니다.", HttpStatus.BAD_GATEWAY);

	private final String code;
	private final String message;
	private final HttpStatus status;

	MessageErrorCode(String code, String message, HttpStatus status) {
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
