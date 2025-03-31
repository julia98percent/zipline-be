package com.zipline.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {
	private Long uid;
	private String grantType;   //ex) Bearer
	private String accessToken;

	@Builder
	public TokenRequestDto(Long uid, String grantType, String accessToken) {
		this.uid = uid;
		this.grantType = grantType;
		this.accessToken = accessToken;
	}
}
