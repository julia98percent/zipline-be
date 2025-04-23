package com.zipline.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zipline.global.jwt.ErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;

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

	public static JSONObject jsonOf(ErrorCode errorCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		jsonObject.put("success", false);
		jsonObject.put("message", errorCode.getMessage());
		jsonObject.put("status", errorCode.getHttpStatus().value());
		jsonObject.put("code", errorCode.getHttpStatus());
		return jsonObject;
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