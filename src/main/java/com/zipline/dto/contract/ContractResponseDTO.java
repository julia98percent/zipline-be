package com.zipline.dto.contract;

import java.time.LocalDate;
import java.util.List;

import com.zipline.entity.contract.Contract;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractResponseDTO {
	private Long uid;
	private String category;
	private LocalDate contractStartDate;
	private LocalDate contractEndDate;
	private String status;
	private Long customerUid;
	private List<String> documentUrls;

	public static ContractResponseDTO of(Contract contract, Long customerUid, List<String> documentUrls) {
		return ContractResponseDTO.builder()
			.uid(contract.getUid())
			.category(contract.getCategory())
			.contractStartDate(contract.getContractStartDate())
			.contractEndDate(contract.getContractEndDate())
			.status(contract.getStatus().name())
			.customerUid(customerUid)
			.documentUrls(documentUrls)
			.build();
	}
}
