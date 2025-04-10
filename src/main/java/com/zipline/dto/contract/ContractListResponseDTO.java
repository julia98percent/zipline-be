package com.zipline.dto.contract;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.zipline.entity.contract.CustomerContract;
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
		private String customerName;
		private String category;
		private LocalDate contractDate;
		private LocalDate contractStartDate;
		private LocalDate contractEndDate;
		private ContractStatus status;

		public ContractListDTO(CustomerContract customerContract) {
			this.uid = customerContract.getContract().getUid();
			this.customerName = customerContract.getCustomer().getName();
			this.category = customerContract.getContract().getCategory();
			this.contractDate = customerContract.getContract().getContractDate();
			this.contractStartDate = customerContract.getContract().getContractStartDate();
			this.contractEndDate = customerContract.getContract().getContractEndDate();
			this.status = customerContract.getContract().getStatus();
		}
	}
}