//Crawler
package com.zipline.global.response;

import lombok.Getter;

@Getter
public class ExceptionResponseDTO {
	private String code;
	private String message;

	private ExceptionResponseDTO(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public static ExceptionResponseDTO of(String code, String message) {
		return new ExceptionResponseDTO(code, message);
	}
}