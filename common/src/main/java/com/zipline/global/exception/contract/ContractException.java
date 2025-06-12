package com.zipline.global.exception.contract;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;

public class ContractException extends BaseException {
	public ContractException(ContractErrorCode errorCode) {
		super(errorCode);
	}
}