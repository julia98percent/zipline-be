package com.zipline.auth.dto;

import java.time.LocalDate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgentRequestDto {

	private String id;
	private String password;
	private String passwordCheck; // 비밀번호 확인
	private String name;
	private String role;
	private String url;
	private LocalDate birthday;
	private String qr;
	private String phoneNo;
	private String email;
	private String certNo;
	private LocalDate certIssueDate;

	public UsernamePasswordAuthenticationToken toAuthentifation() {
		return new UsernamePasswordAuthenticationToken(id, password);
	}
}