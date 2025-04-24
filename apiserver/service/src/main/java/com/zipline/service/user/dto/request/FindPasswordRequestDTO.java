package com.zipline.service.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "비밀번호 찾기 요청 DTO")
@AllArgsConstructor
@Getter
public class FindPasswordRequestDTO {
	@Schema(description = "로그인 ID", example = "agent123")
	@NotBlank(message = "아이디는 필수입니다.")
	private String loginId;

	@Schema(description = "질문 UID", example = "5")
	@NotNull(message = "질문을 선택해주세요.")
	private Long passwordQuestionUid;

	@Schema(description = "답변", example = "쫑이")
	@Size(max = 255, message = "답변은 255자 이하로 입력해주세요.")
	@NotBlank(message = "답변을 입력해주세요.")
	private String answer;
}
