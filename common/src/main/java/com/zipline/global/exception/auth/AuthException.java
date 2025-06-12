package com.zipline.global.exception.auth;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;

public class AuthException extends BaseException {
	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}
}
