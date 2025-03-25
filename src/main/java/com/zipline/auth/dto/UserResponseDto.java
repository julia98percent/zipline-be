package com.zipline.auth.dto;

import com.zipline.auth.entity.Agents;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

	private String id;
	private String name;

	public static UserResponseDto of(Agents agents) {
		return UserResponseDto.builder()
			.id(agents.getId())
			.name(agents.getName())
			.build();
	}
}
