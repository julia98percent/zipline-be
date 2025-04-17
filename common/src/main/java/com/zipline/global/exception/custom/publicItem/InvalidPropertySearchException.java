package com.zipline.global.exception.custom.publicItem;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class InvalidPropertySearchException extends BaseException {
    public InvalidPropertySearchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
