package com.zipline.service.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "아이디 찾기 요청 DTO")
@AllArgsConstructor
@Getter
public class FindUserIdRequestDTO {

	@Schema(description = "회원 이름", example = "홍길동")
	@NotBlank(message = "이름을 입력해주세요.")
	@Size(min = 2, max = 255, message = "이름은 2자 이상 255자 이하로 입력해주세요.")
	@Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름에는 특수문자나 숫자를 포함할 수 없습니다.")
	private String name;

	@Schema(description = "회원 이메일", example = "user@example.com")
	@NotBlank(message = "이메일을 입력해주세요.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 320, message = "이메일은 최대 320자까지 입력 가능합니다.")
	private String email;
}
