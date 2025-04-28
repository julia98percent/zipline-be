package com.zipline.service.contract.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "계약 생성 요청 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContractRequestDTO {

	@Schema(description = "계약 카테고리", example = "월세")
	private String category;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약일", example = "2025-03-10")
	private LocalDate contractDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 시작일", example = "2025-04-01")
	private LocalDate contractStartDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 종료일", example = "2026-04-01")
	private LocalDate contractEndDate;

	@Schema(description = "계약 상태", example = "PENDING")
	private String status;

	@Schema(description = "임대/매도자 고객 UID", example = "1")
	private Long lessorOrSellerUid;

	@Schema(description = "임차/매수자 고객 UID", example = "2")
	private Long lesseeOrBuyerUid;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 종료 예상일", example = "2026-05-01")
	private LocalDate expectedContractEndDate;

}
