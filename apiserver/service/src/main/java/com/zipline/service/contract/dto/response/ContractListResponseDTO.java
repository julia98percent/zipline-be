package com.zipline.service.contract.dto.response;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.enums.ContractCustomerRole;
import com.zipline.entity.enums.ContractStatus;

import lombok.Getter;

@Getter
public class ContractListResponseDTO {
	private List<ContractListDTO> contracts;
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;

	public ContractListResponseDTO(List<ContractListDTO> contracts, Page<?> page) {
		this.contracts = contracts;
		this.page = page.getNumber() + 1;
		this.size = page.getSize();
		this.totalElements = (int)page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.hasNext = page.hasNext();
	}

	@Getter
	public static class ContractListDTO {
		private Long uid;
		private String lessorOrSellerName;
		private String lesseeOrBuyerName;
		private String category;
		private LocalDate contractDate;
		private LocalDate contractStartDate;
		private LocalDate contractEndDate;
		private ContractStatus status;
		private String address;

		public ContractListDTO(Contract contract, List<CustomerContract> customerContracts) {
			this.uid = contract.getUid();
			this.category = String.valueOf(contract.getCategory());
			this.contractDate = contract.getContractDate();
			this.contractStartDate = contract.getContractStartDate();
			this.contractEndDate = contract.getContractEndDate();
			this.status = contract.getStatus();
			this.lessorOrSellerName = extractCustomerNameByRole(customerContracts,
				ContractCustomerRole.LESSOR_OR_SELLER);
			this.lesseeOrBuyerName = extractCustomerNameByRole(customerContracts, ContractCustomerRole.LESSEE_OR_BUYER);
			this.address = contract.getAgentProperty() != null ? contract.getAgentProperty().getAddress() : null;
		}

		private String extractCustomerNameByRole(List<CustomerContract> customerContracts, ContractCustomerRole role) {
			return customerContracts.stream()
				.filter(cc -> cc.getRole() == role)
				.map(cc -> cc.getCustomer().getName())
				.findFirst()
				.orElse(null);
		}
	}
}