package com.zipline.dto.contract;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;

import com.zipline.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "계약 생성 요청 DTO")
@AllArgsConstructor
@Getter
public class ContractRequestDTO {

	@Schema(description = "계약 카테고리", example = "월세")
	private String category;

	@Schema(description = "계약일", example = "2025-03-10")
	private LocalDate contractDate;

	@Schema(description = "계약 시작일", example = "2025-04-01")
	private LocalDate contractStartDate;

	@Schema(description = "계약 종료일", example = "2026-04-01")
	private LocalDate contractEndDate;

	@Schema(description = "계약 상태", example = "PENDING")
	private ContractStatus status;

	@Schema(description = "고객 UID", example = "1")
	private Long customerUid;

	public Contract toEntity(User user, Boolean isDeleted, LocalDateTime createdAt,
							 LocalDateTime updatedAt,
							 LocalDateTime deletedAt) {
		return Contract.builder()
			.category(this.category)
			.contractStartDate(this.contractStartDate)
			.contractEndDate(this.contractEndDate)
			.contractDate(this.contractDate)
			.status(this.status)
			.isDeleted(isDeleted)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.user(user)
			.build();
	}
}
