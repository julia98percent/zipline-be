
package com.zipline.global.exception.custom.publicItem;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class EmptyFileException extends BaseException {
    public EmptyFileException(String message, HttpStatus status) {
        super(message, status);
    }
}
