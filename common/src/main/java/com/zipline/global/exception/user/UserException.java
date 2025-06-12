package com.zipline.global.exception.user;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;

public class UserException extends BaseException {
	public UserException(UserErrorCode errorCode) {
		super(errorCode);
	}
}