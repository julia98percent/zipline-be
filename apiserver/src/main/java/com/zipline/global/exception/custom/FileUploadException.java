package com.zipline.global.exception.custom;

import org.springframework.http.HttpStatus;

public class FileUploadException extends BaseException {

	public FileUploadException(String message, HttpStatus status) {
		super(message, status);
	}
}
