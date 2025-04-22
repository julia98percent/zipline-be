package com.zipline.global.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
	private HttpStatus status;

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
