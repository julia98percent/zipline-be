package com.zipline.global.exception.custom;

public class CrawlingInProgressException extends RuntimeException {
    public CrawlingInProgressException(String message) {
        super(message);
    }
}