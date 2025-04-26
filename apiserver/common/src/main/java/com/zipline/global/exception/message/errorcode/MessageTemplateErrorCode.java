package com.zipline.global.exception.message.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum MessageTemplateErrorCode implements ErrorCode {
	DUPLICATE_TEMPLATE_CATEGORY("MSG-TEMPLATE-001", "해당 카테고리의 문자 템플릿이 이미 존재합니다.", HttpStatus.CONFLICT),
	DUPLICATE_TEMPLATE_NAME("MSG-TEMPLATE-002", "같은 이름의 문자 템플릿이 이미 존재합니다.", HttpStatus.CONFLICT);
	DUPLICATE_TEMPLATE_CATEGORY("MSG-TEMPLATE-003", "해당 카테고리의 문자 템플릿이 이미 존재합니다.", HttpStatus.CONFLICT),
	TEMPLATE_NOT_FOUND("MSG-TEMPLATE-004", "해당하는 문자 템플릿을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus status;

	MessageTemplateErrorCode(String code, String message, HttpStatus status) {
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