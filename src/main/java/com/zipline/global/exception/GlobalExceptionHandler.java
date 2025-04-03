package com.zipline.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.minidev.json.JSONObject;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.BaseException;
import com.zipline.global.jwt.ErrorCode;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<String> handleJwtException(JwtException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = JwtExceptionMessageToErrorCode(e.getMessage());
		JSONObject jsonObject = ApiResponse.jsonOf(errorCode);

		return ResponseEntity.status(errorCode.getHttpStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObject.toJSONString());

	}

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

	private ErrorCode JwtExceptionMessageToErrorCode(String message) {
		if (ErrorCode.JWT_DECODE_FAIL.getMessage().equals(message)) {
			return ErrorCode.JWT_DECODE_FAIL;
		} else if (ErrorCode.JWT_SIGNATURE_FAIL.getMessage().equals(message)) {
			return ErrorCode.JWT_SIGNATURE_FAIL;
		} else {
			return ErrorCode.FORBIDDEN_CLIENT;
		}
	}
}
