package com.zipline.service.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDTO {
	@Schema(description = "로그인 ID", example = "Agent123")
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$",
		message = "아이디는 영문자와 숫자를 조합해야 합니다.")
	private String id;

	@Schema(description = "비밀번호", example = "Secure123!")
	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
		message = "비밀번호는 영문자, 숫자, 특수문자를 모두 포함해야 합니다.")
	private String password;

	@Schema(description = "비밀번호 확인", example = "Secure123!")
	@NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
	private String passwordCheck;

	@Schema(description = "이름", example = "홍길동")
	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Size(min = 2, max = 255, message = "이름은 2자 이상 255자 이하로 입력해주세요.")
	@Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름에는 특수문자나 숫자를 포함할 수 없습니다.")
	private String name;

	@Schema(description = "전화번호", example = "010-1234-5678")
	@NotBlank(message = "전화번호는 필수 입력값입니다.")
	@Pattern(
		regexp = "^(010|011|016|017|018|019)-(\\d{3,4})-(\\d{4})$",
		message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
	)
	private String phoneNo;

	@Schema(description = "이메일", example = "user@example.com")
	@NotBlank(message = "이메일은 필수 입력값입니다.")
	@Email(message = "이메일 형식이 유효하지 않습니다.")
	@Size(max = 320, message = "이메일은 최대 320자까지 입력 가능합니다.")
	private String email;
}
