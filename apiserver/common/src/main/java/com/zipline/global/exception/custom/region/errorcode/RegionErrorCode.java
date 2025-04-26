package com.zipline.global.exception.custom.region.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RegionErrorCode implements ErrorCode {
REGION_NOT_FOUND("REGION-001", "존재하지 않는 지역입니다.", HttpStatus.NOT_FOUND),
    //TODO:
CACHE_ACCESS_UNAVAILABLE("REGION-002", "캐시 접근이 불가능합니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    RegionErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
