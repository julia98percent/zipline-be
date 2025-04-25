package com.zipline.global.exception.label;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.label.errorcode.LabelErrorCode;

public class LabelException extends BaseException {

	public LabelException(LabelErrorCode errorCode) {
		super(errorCode);
	}

}