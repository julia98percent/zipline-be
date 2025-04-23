package com.zipline.service.user.dto.response;

import lombok.Getter;

@Getter
public class FindUserIdResponseDTO {
	private String id;

	public FindUserIdResponseDTO(String id) {
		this.id = id;
	}
}
