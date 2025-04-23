package com.zipline.global.exception.custom.publicItem;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class FailedDirCreationException extends BaseException {
    public FailedDirCreationException(String message, HttpStatus status) {
        super(message, status);
    }
}
