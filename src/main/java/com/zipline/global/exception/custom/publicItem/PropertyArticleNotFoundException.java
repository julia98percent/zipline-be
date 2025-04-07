package com.zipline.global.exception.custom.publicItem;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class PropertyArticleNotFoundException extends BaseException {
    public PropertyArticleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

