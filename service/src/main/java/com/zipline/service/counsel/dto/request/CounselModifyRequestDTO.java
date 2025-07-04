package com.zipline.service.counsel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Tag(name = "상담 수정 요청", description = "상담을 생성하기 위한 요청 DTO")
@Getter
public class CounselModifyRequestDTO {

  @Schema(description = "상담 제목", example = "1:1 맞춤 상담", required = true)
  @Size(max = 20, message = "상담 제목의 최대 길이는 20자 입니다.")
  @NotBlank(message = "상담 제목의 최소 길이는 1자 입니다.")
  private String title;

  @Schema(description = "상담 일시", example = "2025-04-10T15:00:00", required = true)
  @NotNull(message = "상담 일자는 필수값 입니다.")
  private LocalDateTime counselDate;

  @Valid
  @Schema(description = "상담 내용", example = "관심 지역 - 서울 강남구, 2룸 이상", required = true)
  @Size(max = 500, message = "상담 내용의 최대 길이는 500자 입니다.")
  @NotBlank(message = "상담 내용은 필수입니다.")
  private String content;

  @Schema(description = "상담 종류", example = "임대", required = true)
  @NotBlank(message = "상담 종류는 필수값 입니다.")
  private String type;

  @Schema(description = "의뢰 기한", example = "2025-02-01")
  private LocalDate dueDate;

  @Schema(description = "의뢰 완료 여부", example = "true")
  private boolean completed;

}