package com.zipline.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zipline.global.exception.custom.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(BaseException e) {
		log.error(e.getMessage(), e);
		ExceptionResponseDTO response = ExceptionResponseDTO.of(e.getStatus(), e.getMessage());
		return ResponseEntity.status(e.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);
		ExceptionResponseDTO response = ExceptionResponseDTO.of(HttpStatus.BAD_REQUEST,
			e.getBindingResult().getFieldError().getDefaultMessage());
		return ResponseEntity.status(e.getStatusCode()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(Exception e) {
		log.error(e.getMessage(), e);
		ExceptionResponseDTO response = ExceptionResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR,
			"서버 에러가 발생하였습니다.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
