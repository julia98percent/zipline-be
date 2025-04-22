package com.zipline.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "아이디 찾기 요청 DTO")
@AllArgsConstructor
@Getter
public class FindUserIdRequestDTO {

	@Schema(description = "회원 이름", example = "홍길동")
	private final String name;

	@Schema(description = "회원 이메일", example = "honggildong@example.com")
	private final String email;
}
