package com.zipline.service.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "비밀번호 재설정 요청 DTO")
@AllArgsConstructor
@Getter
public class ResetPasswordRequestDTO {
	@Schema(description = "비밀번호 재설정 토큰", example = "token")
	@NotBlank(message = "재설정 토큰이 필요합니다.")
	private String token;

	@Schema(description = "새 비밀번호", example = "NewSecure123!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	private String newPassword;

	@Schema(description = "새 비밀번호 확인", example = "NewSecure123!")
	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	private String newPasswordCheck;
}
