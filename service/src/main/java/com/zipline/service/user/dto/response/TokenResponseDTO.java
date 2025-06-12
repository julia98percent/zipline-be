package com.zipline.service.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
	private Long uid;
	private String grantType;   //ex) Bearer
	private String accessToken;
}
