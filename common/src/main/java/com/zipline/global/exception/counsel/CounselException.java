package com.zipline.global.exception.counsel;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.counsel.errorcode.CounselErrorCode;

public class CounselException extends BaseException {
	public CounselException(CounselErrorCode errorCode) {
		super(errorCode);
	}
}