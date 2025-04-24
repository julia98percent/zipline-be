package com.zipline.global.exception.customer;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;

public class CustomerException extends BaseException {
	public CustomerException(CustomerErrorCode errorCode) {
		super(errorCode);
	}
}