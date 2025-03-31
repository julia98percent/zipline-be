package com.zipline.auth.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

	private String id;
	private String password;
	private String passwordCheck; // 비밀번호 확인
	private String name;
	private String url;
	private Integer birthday;
	private String phoneNo;
	private String email;
	private Integer noticeMonth;

	public UsernamePasswordAuthenticationToken toAuthentifation() {
		return new UsernamePasswordAuthenticationToken(id, password);
	}
}