package com.zipline.global.exception.custom;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends BaseException {

	public FileUploadException(String message, HttpStatus status) {
		super(message, status);
	}
}