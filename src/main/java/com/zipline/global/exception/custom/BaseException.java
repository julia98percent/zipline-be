package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
	private HttpStatus status;

	public BaseException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
