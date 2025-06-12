package com.zipline.global.exception.message;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.message.errorcode.MessageTemplateErrorCode;

public class MessageTemplateException extends BaseException {

	public MessageTemplateException(MessageTemplateErrorCode errorCode) {
		super(errorCode);
	}
}
