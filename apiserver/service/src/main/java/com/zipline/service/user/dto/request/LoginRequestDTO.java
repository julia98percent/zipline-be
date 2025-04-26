package com.zipline.service.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
	@Schema(description = "로그인 ID", example = "Agent123")
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$",
		message = "아이디는 영문자와 숫자를 조합해야 합니다.")
	private String id;

	@Schema(description = "비밀번호", example = "Secure123!")
	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()\\+|=])[A-Za-z\\d~!@#$%^&*()\\+|=]{8,16}$",
		message = "비밀번호는 영문자, 숫자, 특수문자를 모두 포함해야 합니다.")
	private String password;
}
