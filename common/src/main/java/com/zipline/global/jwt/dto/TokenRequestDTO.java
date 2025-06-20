package com.zipline.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDTO {
	private Long uid;
	private String grantType;   //ex) Bearer
	private String accessToken;
	private String refreshToken;

	@Builder
	public TokenRequestDTO(Long uid, String grantType, String accessToken, String refreshToken) {
		this.uid = uid;
		this.grantType = grantType;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}

