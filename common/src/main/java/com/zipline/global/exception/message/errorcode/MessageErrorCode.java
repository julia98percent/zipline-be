package com.zipline.global.exception.message.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MessageErrorCode implements ErrorCode {
	MESSAGE_SEND_FAILED("MSG-001", "문자 전송에 실패하였습니다.", HttpStatus.BAD_GATEWAY),
	MESSAGE_HISTORY_INTERNAL_FAILED("MSG-002", "문자 전송 목록을 가져오는데에 실패하였습니다.",  HttpStatus.BAD_REQUEST),
	MESSAGE_HISTORY_EXTERNAL_FAILED("MSG-003", "문자 전송 목록을 가져오는데에 실패하였습니다.",  HttpStatus.BAD_GATEWAY),
	MESSAGE_RESERVATION_FAILED("MSG-004", "예약 문자 생성에 실패했습니다.",  HttpStatus.BAD_GATEWAY),
	EXPIRED_NOTI_MESSAGE_TEMPLATE_NOT_FOUND("MSG-005", "해당하는 만료 알림 문자 템플릿을 찾을 수 없습니다.", HttpStatus.BAD_GATEWAY),
	BIRTHDAY_MESSAGE_TEMPLATE_NOT_FOUND("MSG-006", "해당하는 생일 축하 문자 템플릿을 찾을 수 없습니다.", HttpStatus.BAD_GATEWAY);

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
