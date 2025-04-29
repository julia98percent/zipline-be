package com.zipline.global.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "상담 목록 필터 요청 DTO")
public class CounselFilterRequestDTO {
	@Schema(description = "검색어 (이름 또는 전화번호)", example = "홍길동")
	private String search;

	@Schema(description = "조회 시작일 (포함)", example = "2025-04-01")
	private LocalDate startDate;

	@Schema(description = "조회 종료일 (포함)", example = "2025-04-30")
	private LocalDate endDate;
}
