package com.zipline.service.contract.dto.request;

import java.math.BigInteger;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.enums.PropertyType;
import com.zipline.entity.user.User;
import com.zipline.global.exception.contract.ContractException;
import com.zipline.global.exception.contract.errorcode.ContractErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "계약 생성 요청 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContractRequestDTO {

	@Schema(description = "계약 카테고리", example = "SALE")
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

	@Schema(description = "계약 상태", example = "IN_PROGRESS")
	private String status;

	@PositiveOrZero(message = "보증금은 0 이상의 값이어야 합니다.")
	@Schema(description = "보증금", example = "50000000")
	private BigInteger deposit;

	@PositiveOrZero(message = "월세는 0 이상의 값이어야 합니다.")
	@Schema(description = "월세", example = "1000000")
	private BigInteger monthlyRent;

	@PositiveOrZero(message = "매매 가격은 0 이상의 값이어야 합니다.")
	@Schema(description = "매매 가격", example = "800000000")
	private BigInteger price;

	@Schema(description = "임대/매도자 고객 UID", example = "1")
	private Long lessorOrSellerUid;

	@Schema(description = "임차/매수자 고객 UID", example = "2")
	private Long lesseeOrBuyerUid;

	@Schema(description = "매물 UID", example = "1")
	private Long propertyUid;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 종료 예상일", example = "2026-05-01")
	private LocalDate expectedContractEndDate;

	public Contract toEntity(User user, AgentProperty agentProperty, ContractStatus status, PropertyType category) {
		return Contract.builder()
			.user(user)
			.agentProperty(agentProperty)
			.deposit(this.deposit)
			.monthlyRent(this.monthlyRent)
			.price(this.price)
			.category(category)
			.contractDate(this.contractDate)
			.contractStartDate(this.contractStartDate)
			.contractEndDate(this.contractEndDate)
			.expectedContractEndDate(this.expectedContractEndDate)
			.status(status)
			.build();
	}

	public void validateDateOrder() {
		if (contractDate != null && contractStartDate != null && contractDate.isAfter(contractStartDate)) {
			throw new ContractException(ContractErrorCode.CONTRACT_DATE_AFTER_START_DATE);
		}
		if (contractStartDate != null && contractEndDate != null && !contractStartDate.isBefore(contractEndDate)) {
			throw new ContractException(ContractErrorCode.CONTRACT_START_DATE_NOT_BEFORE_END_DATE);
		}
	}

	public void validateProperty() {
		if (propertyUid == null) {
			throw new ContractException(ContractErrorCode.PROPERTY_REQUIRED);
		}
	}

	public void validateDistinctParties() {
		if (lessorOrSellerUid != null && lessorOrSellerUid.equals(lesseeOrBuyerUid)) {
			throw new ContractException(ContractErrorCode.SAME_CUSTOMER_FOR_BOTH_PARTIES);
		}
	}

}
