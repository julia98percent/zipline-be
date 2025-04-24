package com.zipline.global.exception.survey.errorcode;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.ErrorCode;

public enum SurveyErrorCode implements ErrorCode {
	SURVEY_NOT_FOUND("SURVEY-001", "해당하는 설문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	CHOICE_NOT_ALLOWED_FOR_NON_OBJECTIVE("SURVEY-002", "객관식이 아닌 문항에는 선택지를 추가할 수 없습니다.", HttpStatus.BAD_REQUEST),
	CHOICE_INVALID("SURVEY-003", "올바르지 않은 선택지가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
	CHOICE_SINGLE_ONLY("SURVEY-004", "단일 선택 문항에는 하나의 선택지만 허용됩니다.", HttpStatus.BAD_REQUEST),

	QUESTION_NOT_FOUND("SURVEY-005", "해당하는 문항이 없습니다.", HttpStatus.BAD_REQUEST),
	QUESTION_MAPPING_NOT_FOUND("SURVEY-006", "파일과 매핑되는 유효한 문항이 없습니다.", HttpStatus.BAD_REQUEST),
	QUESTION_TYPE_UNSUPPORTED("SURVEY-007", "지원하지 않는 질문 타입입니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

	SurveyErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}
}
