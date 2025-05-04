package com.zipline.global.exception.task;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;


public class TaskException extends BaseException {
    public TaskException(TaskErrorCode errorCode) {
        super(errorCode);
    }
}
