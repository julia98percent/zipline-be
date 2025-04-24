package com.zipline.global.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.global.exception.ErrorCode;
import com.zipline.global.response.ExceptionResponseDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtExceptionHandler {

	private final ObjectMapper objectMapper;

	public void handle(HttpServletResponse response, ErrorCode errorCode) {
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(errorCode.getStatus().value());

			ExceptionResponseDTO body = ExceptionResponseDTO.of(errorCode.getCode(),
				errorCode.getMessage());

			String json = objectMapper.writeValueAsString(body);
			response.getWriter().write(json);
		} catch (IOException ex) {
			throw new RuntimeException("JWT 예외 응답 처리 중 오류 발생", ex);
		}
	}
}
