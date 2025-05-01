package com.zipline.global.exception.region;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.region.errorcode.RegionErrorCode;

public class RegionException extends BaseException {
    public RegionException(RegionErrorCode errorCode) {
        super(errorCode);
    }

}
