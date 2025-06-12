package com.zipline.service.contract.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;

import lombok.Getter;

@Getter
public class ContractPropertyResponseDTO {
	private Long contractUid;
	private String contractCategory;
	private LocalDate contractStartDate;
	private LocalDate contractEndDate;
	private LocalDate contractDate;
	private List<CustomerInfo> customers;

	public ContractPropertyResponseDTO(Contract contract, List<CustomerContract> customerContracts) {
		if (contract == null) {
			this.contractUid = null;
			this.contractCategory = null;
			this.contractStartDate = null;
			this.contractEndDate = null;
			this.contractDate = null;
			this.customers = List.of();
			return;
		}
		this.contractUid = contract.getUid();
		this.contractCategory = contract.getCategory() != null ? contract.getCategory().name() : null;
		this.contractStartDate = contract.getContractStartDate();
		this.contractEndDate = contract.getContractEndDate();
		this.contractDate = contract.getContractDate();
		this.customers = customerContracts.stream().map(CustomerInfo::new).toList();
	}

	@Getter
	public static class CustomerInfo {
		private Long customerUid;
		private String customerName;
		private String customerRole;

		public CustomerInfo(CustomerContract customerContract) {
			this.customerUid = customerContract.getCustomer().getUid();
			this.customerName = customerContract.getCustomer().getName();
			this.customerRole = customerContract.getRole().name();
		}
	}
}
