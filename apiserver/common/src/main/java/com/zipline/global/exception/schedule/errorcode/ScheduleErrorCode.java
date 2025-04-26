package com.zipline.global.exception.schedule.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ScheduleErrorCode implements ErrorCode {
	INVALID_SCHEDULE_TIME("MSG-001", "시작 시간이 종료 시간보다 늦을 수 없습니다.", HttpStatus.BAD_REQUEST);
	private final String code;
	private final String message;
	private final HttpStatus status;

	ScheduleErrorCode(String code, String message, HttpStatus status) {
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