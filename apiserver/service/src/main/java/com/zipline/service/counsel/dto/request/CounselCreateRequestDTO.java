package com.zipline.service.counsel.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Tag(name = "상담 생성 요청", description = "상담을 생성하기 위한 요청 DTO")
@NoArgsConstructor
@Getter
public class CounselCreateRequestDTO {

	@Schema(description = "상담 제목", example = "1:1 맞춤 상담", required = true)
	@Size(max = 20, message = "상담 제목의 최대 길이는 20자 입니다.")
	@NotBlank(message = "상담 제목의 최소 길이는 1자 입니다.")
	private String title;

	@Schema(description = "상담 일시", example = "2025-04-10T15:00:00", required = true)
	@NotNull(message = "상담 일자는 필수값 입니다.")
	private LocalDateTime counselDate;

	@Schema(description = "상담 종류", example = "임대", required = true)
	@NotBlank(message = "상담 종류는 필수값 입니다.")
	private String type;

	@Schema(description = "의뢰 기한", example = "2025-02-01")
	private LocalDate dueDate;

	@Schema(description = "상담 관련 매물 UID", example = "1")
	private Long propertyUid;

	@Valid
	@Schema(description = "상담 문항 리스트", required = true)
	@NotEmpty(message = "상담에는 최소 한개의 문항이 필요합니다.")
	private List<CounselDetailDTO> counselDetails;

	@Schema(description = "상담 문항 정보")
	@Getter
	public static class CounselDetailDTO {

		@Schema(description = "상담 질문", example = "관심있는 매물의 유형은?", required = true)
		@Size(max = 100, message = "질문의 최대 길이는 100자 입니다.")
		@NotBlank(message = "질문 내용은 필수입니다.")
		private String question;

		@Schema(description = "상담 답변", example = "월세에 관심있습니다.", required = true)
		@Size(max = 200, message = "답변의 최대 길이는 200자 입니다.")
		@NotBlank(message = "답변 내용은 필수입니다.")
		private String answer;
	}
}
