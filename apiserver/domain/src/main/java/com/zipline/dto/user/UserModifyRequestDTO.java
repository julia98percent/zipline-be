package com.zipline.dto.user;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserModifyRequestDTO {

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

	@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
	@Size(max = 255, message = "URL은 최대 255자까지 입력 가능합니다.")
	private String url;

	@Schema(description = "계약 만료 문자 기준 달", example = "3")
	@NotNull(message = "계약 만료 기준 달은 필수 입력값입니다.")
	@Min(value = 1, message = "1 이상이어야 합니다.")
	private Integer noticeMonth;

	@Schema(description = "계약 만료 문자 발송 시간", example = "09:00")
	@NotNull(message = "계약 만료 발송 시간은 필수 입력값입니다.")
	private LocalTime noticeTime;
}
