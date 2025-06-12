package com.zipline.global.exception.external;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.ErrorCode;

public class ExternalException extends BaseException {

	public ExternalException(ErrorCode errorCode) {
		super(errorCode);
	}
}
