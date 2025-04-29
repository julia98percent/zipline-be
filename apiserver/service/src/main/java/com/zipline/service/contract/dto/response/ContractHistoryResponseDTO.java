package com.zipline.service.contract.dto.response;

import java.time.LocalDate;

import com.zipline.entity.contract.ContractHistory;

import lombok.Getter;

@Getter
public class ContractHistoryResponseDTO {
	private String prevStatus;
	private String currentStatus;
	private LocalDate changedAt;

	public ContractHistoryResponseDTO(String prevStatus, String currentStatus, LocalDate changedAt) {
		this.prevStatus = prevStatus;
		this.currentStatus = currentStatus;
		this.changedAt = changedAt;
	}

	public static ContractHistoryResponseDTO from(ContractHistory history) {
		return new ContractHistoryResponseDTO(
			history.getPrevStatus(),
			history.getCurrentStatus(),
			history.getChangedAt()
		);
	}
}
