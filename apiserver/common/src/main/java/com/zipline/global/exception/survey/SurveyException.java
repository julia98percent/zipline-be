package com.zipline.global.exception.survey;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.survey.errorcode.SurveyErrorCode;

public class SurveyException extends BaseException {
	public SurveyException(SurveyErrorCode errorCode) {
		super(errorCode);
	}
}