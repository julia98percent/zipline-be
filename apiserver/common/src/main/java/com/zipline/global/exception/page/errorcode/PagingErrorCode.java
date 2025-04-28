package com.zipline.global.exception.page.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PagingErrorCode implements ErrorCode {
    PAGING_SORT_ERROR("PAGING-001", "정렬 방향 입력이 잘못되었습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    PagingErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

    @Override
    public HttpStatus getStatus() { return status; }
}
