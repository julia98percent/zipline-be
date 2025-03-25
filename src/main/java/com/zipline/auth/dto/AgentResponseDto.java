package com.zipline.auth.dto;

import java.time.LocalDate;

import com.zipline.auth.entity.Agent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgentResponseDto {

	private String id;
	private String name;
	private String role;
	private String url;
	private LocalDate birthday;
	private String qr;
	private String phoneNo;
	private String email;
	private String certNo;
	private LocalDate certIssueDate;

	public static AgentResponseDto of(Agent agent) {
		return AgentResponseDto.builder()
			.id(agent.getId())
			.name(agent.getName())
			.role(agent.getRole())
			.url(agent.getUrl())
			.birthday(agent.getBirthday())
			.qr(agent.getQr())
			.phoneNo(agent.getPhoneNo())
			.email(agent.getEmail())
			.certNo(agent.getCertNo())
			.certIssueDate(agent.getCertIssueDate())
			.build();
	}
}
