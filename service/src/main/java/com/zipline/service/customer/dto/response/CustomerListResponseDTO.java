package com.zipline.service.customer.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.zipline.entity.customer.Customer;

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
		private List<LabelDTO> labels;
		private String legalDistrictCode;
		private String birthday;

		public CustomerResponseDTO(Customer customer) {
			this.uid = customer.getUid();
			this.name = customer.getName();
			this.phoneNo = customer.getPhoneNo();
			this.trafficSource = customer.getTrafficSource();
			this.isTenant = customer.isTenant();
			this.isLandlord = customer.isLandlord();
			this.isBuyer = customer.isBuyer();
			this.isSeller = customer.isSeller();
			this.labels = customer.getLabelCustomers() != null ? customer.getLabelCustomers()
				.stream()
				.map(lc -> new LabelDTO(lc.getLabel().getUid(), lc.getLabel().getName()))
				.toList() : List.of();
			this.legalDistrictCode = customer.getLegalDistrictCode();
			this.birthday = customer.getBirthday();
		}

		@Getter
		public static class LabelDTO {
			private Long uid;
			private String name;

			public LabelDTO(Long uid, String name) {
				this.uid = uid;
				this.name = name;
			}
		}
	}
}
