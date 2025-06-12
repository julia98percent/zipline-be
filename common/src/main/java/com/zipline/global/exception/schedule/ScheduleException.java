package com.zipline.global.exception.schedule;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.schedule.errorcode.ScheduleErrorCode;

public class ScheduleException extends BaseException {
	public ScheduleException(ScheduleErrorCode errorCode) {
		super(errorCode);
	}
}