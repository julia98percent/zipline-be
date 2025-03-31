package com.zipline.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
	private Long uid;
	private String grantType;   //ex) Bearer
	private String accessToken;
}
