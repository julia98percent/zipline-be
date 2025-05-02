package com.zipline.global.exception.task.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum TaskErrorCode implements ErrorCode {
    TASK_ALREADY_RUNNING("TASK-000", "이미 실행 중인 작업이 있습니다.", HttpStatus.BAD_REQUEST),
    TASK_NOT_FOUND("TASK-001", "존재하지 않는 작업입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    TaskErrorCode(String code, String message, HttpStatus status) {
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
