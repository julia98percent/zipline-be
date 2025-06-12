package com.zipline.service.contract.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractHistory;
import com.zipline.entity.contract.CustomerContract;

import lombok.Getter;

@Getter
public class ContractPropertyHistoryResponseDTO {
	private Long contractUid;
	private String contractCategory;
	private LocalDate endDate;
	private String contractStatus;
	private List<CustomerInfo> customers;

	public ContractPropertyHistoryResponseDTO(Contract contract, ContractHistory contractHistory,
		List<CustomerContract> customerContracts) {
		this.contractUid = contract.getUid();
		this.contractCategory = contract.getCategory().name();
		this.endDate = contractHistory.getChangedAt();
		this.contractStatus = contractHistory.getCurrentStatus();
		this.customers = customerContracts.stream().map(ContractPropertyHistoryResponseDTO.CustomerInfo::new).toList();
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
