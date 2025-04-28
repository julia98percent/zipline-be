package com.zipline.service.contract.dto.response;

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
	private LocalDate expectedContractEndDate;
	private String status;
	private String lessorOrSellerName;
	private String lesseeOrBuyerName;
	private List<DocumentDTO> documents;

	@Getter
	public static class DocumentDTO {
		private String fileName;
		private String fileUrl;

		public DocumentDTO(String fileName, String fileUrl) {
			this.fileName = fileName;
			this.fileUrl = fileUrl;
		}
	}

	public static ContractResponseDTO of(Contract contract, String lessorOrSellerName, String lesseeOrBuyerName,
		List<DocumentDTO> documents) {
		return ContractResponseDTO.builder()
			.uid(contract.getUid())
			.category(contract.getCategory())
			.contractStartDate(contract.getContractStartDate())
			.contractEndDate(contract.getContractEndDate())
			.expectedContractEndDate(contract.getExpectedContractEndDate())
			.status(contract.getStatus().name())
			.lessorOrSellerName(lessorOrSellerName)
			.lesseeOrBuyerName(lesseeOrBuyerName)
			.documents(documents)
			.build();
	}
}
