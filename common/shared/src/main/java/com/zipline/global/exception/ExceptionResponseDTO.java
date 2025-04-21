package com.zipline.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionResponseDTO {
	private int code;
	private String message;

	private ExceptionResponseDTO(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static ExceptionResponseDTO of(HttpStatus httpStatus, String message) {
		return new ExceptionResponseDTO(httpStatus.value(), message);
	}
}
