package com.zipline.global.exception.migration;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.migration.errorcode.MigrationErrorCode;

public class MigrationException extends BaseException {
    public MigrationException(MigrationErrorCode errorCode) {
        super(errorCode);
    }
}
