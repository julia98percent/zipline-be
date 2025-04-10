
package com.zipline.global.exception.custom.publicItem;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.BaseException;

public class FileReadException extends BaseException {
    public FileReadException(String message, HttpStatus status) {
        super(message, status);
    }
}
