package com.zipline.global.exception.agentProperty;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.agentProperty.errorcode.PropertyErrorCode;

public class PropertyException extends BaseException {
	public PropertyException(PropertyErrorCode errorCode) {
		super(errorCode);
	}
}