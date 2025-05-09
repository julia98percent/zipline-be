package com.zipline.global.exception.migration.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MigrationErrorCode implements ErrorCode {
    MIGRATION_FAILED("MIGRATION-000", "마이그레이션 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    TASK_ALREADY_RUNNING("MIGRATION-001", "이미 실행 중인 마이그레이션 작업이 있습니다.", HttpStatus.BAD_REQUEST),
    TASK_NOT_FOUND("MIGRATION-002", "존재하지 않는 마이그레이션 작업입니다.", HttpStatus.NOT_FOUND);


    private final String code;
    private final String message;
    private final HttpStatus status;

    MigrationErrorCode(String code, String message, HttpStatus status) {
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
