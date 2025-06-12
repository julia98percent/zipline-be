package com.zipline.global.exception.common;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.common.errorcode.CommonErrorCode;

public class FileUploadException extends BaseException {
	public FileUploadException(CommonErrorCode errorCode) {
		super(errorCode);
	}
}