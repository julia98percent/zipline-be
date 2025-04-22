package com.zipline.dto.user;

import com.zipline.entity.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserIdResponseDTO {
	private String id;

	public static FindUserIdResponseDTO findId(User user) {
		return FindUserIdResponseDTO.builder()
			.id(user.getId())
			.build();
	}
}
