package com.zipline.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ExceptionResponseDTO<T> {
	private String code;
	private String message;
	private T errors;

	private ExceptionResponseDTO(String code, String message, T errors) {
		this.code = code;
		this.message = message;
		this.errors = errors;
	}

	public static ExceptionResponseDTO of(String code, String message) {
		return new ExceptionResponseDTO(code, message, null);
	}

	public static <T> ExceptionResponseDTO of(String code, String message, T errors) {
		return new ExceptionResponseDTO(code, message, errors);
	}
}
