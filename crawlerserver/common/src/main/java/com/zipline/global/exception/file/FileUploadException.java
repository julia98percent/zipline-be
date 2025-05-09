package com.zipline.global.exception.file;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.file.errorcode.FileErrorCode;

public class FileUploadException extends BaseException {
	public FileUploadException(FileErrorCode errorCode) {
		super(errorCode);
	}
}