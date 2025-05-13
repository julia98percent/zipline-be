package com.zipline.global.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.zipline.global.exception.common.errorcode.CommonErrorCode;
import com.zipline.global.exception.excel.ExcelException;
import com.zipline.global.response.ExceptionResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ExcelException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(ExcelException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = e.getErrorCode();
		ExceptionResponseDTO response = ExceptionResponseDTO.of(errorCode.getCode(), errorCode.getMessage(),
			e.getDetails());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(BaseException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = e.getErrorCode();
		ExceptionResponseDTO response = ExceptionResponseDTO.of(errorCode.getCode(), e.getMessage());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);
		String message = e.getBindingResult().getFieldError().getDefaultMessage();
		ErrorCode errorCode = CommonErrorCode.INVALID_INPUT_VALUE;
		return ResponseEntity.status(errorCode.getStatus())
			.body(ExceptionResponseDTO.of(errorCode.getCode(), message));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(HttpRequestMethodNotSupportedException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = CommonErrorCode.METHOD_NOT_ALLOWED;
		return ResponseEntity.status(errorCode.getStatus())
			.body(ExceptionResponseDTO.of(errorCode.getCode(), errorCode.getMessage()));
	}

	@ExceptionHandler({MaxUploadSizeExceededException.class, FileSizeLimitExceededException.class})
	public ResponseEntity<ExceptionResponseDTO> handleException(RuntimeException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = CommonErrorCode.FILE_TOO_LARGE;
		ExceptionResponseDTO response = ExceptionResponseDTO.of(errorCode.getCode(), errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(Exception e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		ExceptionResponseDTO response = ExceptionResponseDTO.of(errorCode.getCode(), e.getMessage());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}
}