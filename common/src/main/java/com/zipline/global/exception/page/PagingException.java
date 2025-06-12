package com.zipline.global.exception.page;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.page.errorcode.PagingErrorCode;

public class PagingException extends BaseException {

    public PagingException(PagingErrorCode errorCode) {
        super(errorCode);
    }
}
