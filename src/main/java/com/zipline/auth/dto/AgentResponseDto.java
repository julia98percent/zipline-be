package com.zipline.auth.dto;

import com.zipline.auth.entity.Agent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgentResponseDto {

	private String id;
	private String name;

	public static AgentResponseDto of(Agent agent) {
		return AgentResponseDto.builder()
			.id(agent.getId())
			.name(agent.getName())
			.build();
	}
}
