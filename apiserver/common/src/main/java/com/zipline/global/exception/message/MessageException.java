package com.zipline.global.exception.message;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;

public class MessageException extends BaseException {
	public MessageException(MessageErrorCode errorCode) {
		super(errorCode);
	}
}