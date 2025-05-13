package com.zipline.global.exception.excel;

import java.util.List;
import java.util.Map;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.ErrorCode;
import com.zipline.global.exception.excel.errorcode.ExcelErrorCode;

public class ExcelException extends BaseException {
	private List<Map<String, Object>> details;

	public ExcelException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ExcelException(ErrorCode errorCode, int rowNum, String field, Object value, String message) {
		super(errorCode);
		this.details = List.of(Map.of("rowNum", rowNum, "field", field, "value", value, "message", message));
	}

	public ExcelException(ExcelErrorCode errorCode, List<Map<String, Object>> details) {
		super(errorCode);
		this.details = details;
	}

	public List<Map<String, Object>> getDetails() {
		return details;
	}
}
