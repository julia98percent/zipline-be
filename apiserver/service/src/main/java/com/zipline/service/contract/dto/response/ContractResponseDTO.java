package com.zipline.service.contract.dto.response;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.enums.ContractCustomerRole;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
public class ContractResponseDTO {
	private Long uid;
	private String category;
	private BigInteger deposit;
	private BigInteger monthlyRent;
	private BigInteger price;
	private LocalDate contractStartDate;
	private LocalDate contractEndDate;
	private LocalDate expectedContractEndDate;
	private LocalDate contractDate;
	private String status;
	private List<String> lessorOrSellerNames;
	private List<String> lesseeOrBuyerNames;

	private List<DocumentDTO> documents;
	private String propertyAddress;

	@Getter
	@NoArgsConstructor
	@Setter
	public static class DocumentDTO {
		private String fileName;
		private String fileUrl;

		public DocumentDTO(String fileName, String fileUrl) {
			this.fileName = fileName;
			this.fileUrl = fileUrl;
		}
	}

	public static ContractResponseDTO of(Contract contract, List<CustomerContract> customerContracts,
		List<DocumentDTO> documents) {

		List<String> lessorOrSellerNames = customerContracts.stream()
			.filter(cc -> cc.getRole() == ContractCustomerRole.LESSOR_OR_SELLER)
			.map(cc -> cc.getCustomer().getName())
			.toList();

		List<String> lesseeOrBuyerNames = customerContracts.stream()
			.filter(cc -> cc.getRole() == ContractCustomerRole.LESSEE_OR_BUYER)
			.map(cc -> cc.getCustomer().getName())
			.toList();

		return ContractResponseDTO.builder()
			.uid(contract.getUid())
			.category(contract.getCategory() != null ? String.valueOf(contract.getCategory()) : null)
			.price(contract.getPrice())
			.deposit(contract.getDeposit())
			.monthlyRent(contract.getMonthlyRent())
			.contractStartDate(contract.getContractStartDate())
			.contractEndDate(contract.getContractEndDate())
			.expectedContractEndDate(contract.getExpectedContractEndDate())
			.contractDate(contract.getContractDate())
			.status(contract.getStatus().name())
			.lessorOrSellerNames(lessorOrSellerNames)
			.lesseeOrBuyerNames(lesseeOrBuyerNames)
			.documents(documents)
			.propertyAddress(contract.getAgentProperty().getAddress())
			.build();
	}

}
