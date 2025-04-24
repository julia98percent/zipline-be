package com.zipline.global.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private boolean success;
	private int code;
	private String message;
	private T data;

	private ApiResponse(boolean success, int code, String message, T data) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
		return new ApiResponse<>(true, status.value(), message, data);
	}

	public static <T> ApiResponse<T> success(HttpStatus status, String message) {
		return new ApiResponse<>(true, status.value(), message, null);
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		return new ApiResponse<>(true, 200, message, data);
	}

	public static <T> ApiResponse<T> ok(String message) {
		return new ApiResponse<>(true, 200, message, null);
	}

	public static <T> ApiResponse<T> create(String message, T data) {
		return new ApiResponse<>(true, 201, message, data);
	}

	public static <T> ApiResponse<T> create(String message) {
		return new ApiResponse<>(true, 201, message, null);
	}
}