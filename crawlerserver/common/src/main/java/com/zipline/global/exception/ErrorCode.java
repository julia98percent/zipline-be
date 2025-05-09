package com.zipline.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String getCode();       // "E001" 같은 실제 코드

	String getMessage();    // 기본 에러 메시지

	HttpStatus getStatus(); // 응답 상태 코드
}
