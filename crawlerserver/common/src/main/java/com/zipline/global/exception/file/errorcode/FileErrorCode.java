package com.zipline.global.exception.file.errorcode;

import com.zipline.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FileErrorCode implements ErrorCode {
	FILE_ERROR_CODE("FILE-000", "파일 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_EMPTY("FILE-004", "파일이 비어있습니다.", HttpStatus.BAD_REQUEST),
	FILE_WRONG_TEXT_FORMATE("FILE-005","잘못된 텍스트 포멧입니다" ,HttpStatus.BAD_REQUEST ),
	FILE_READ_FAILED("FILE-006","파일 읽기 중 오류가 발생했습니다.",HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_DIR_CREATE_FAILED("FILE-007","파일 업로드를 위한 디렉토리 생성에 실패했습니다",HttpStatus.INTERNAL_SERVER_ERROR),
	FILE_SAVE_FAILED("FILE-008","파일 저장 중 오류가 발생했습니다",HttpStatus.INTERNAL_SERVER_ERROR)
	;
	
	private final String code;
	private final String message;
	private final HttpStatus status;

	FileErrorCode(String code, String message, HttpStatus status) {
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
