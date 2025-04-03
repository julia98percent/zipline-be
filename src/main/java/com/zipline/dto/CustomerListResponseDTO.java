package com.zipline.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.zipline.entity.Customer;

import lombok.Getter;

@Getter
public class CustomerListResponseDTO {
	private List<CustomerResponseDTO> customers;
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;

	public CustomerListResponseDTO(List<CustomerResponseDTO> customers, Page page) {
		this.customers = customers;
		this.page = page.getNumber() + 1;
		this.size = page.getSize();
		this.totalElements = (int)page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.hasNext = page.hasNext();
	}

	@Getter
	public static class CustomerResponseDTO {
		private Long uid;
		private String name;
		private String phoneNo;
		private String trafficSource;
		private boolean isTenant;
		private boolean isLandlord;
		private boolean isBuyer;
		private boolean isSeller;

		public CustomerResponseDTO(Customer customer) {
			this.uid = customer.getUid();
			this.name = customer.getName();
			this.phoneNo = customer.getPhoneNo();
			this.trafficSource = customer.getTrafficSource();
			this.isTenant = customer.isTenant();
			this.isLandlord = customer.isLandlord();
			this.isBuyer = customer.isBuyer();
			this.isSeller = customer.isSeller();
		}
	}
}
