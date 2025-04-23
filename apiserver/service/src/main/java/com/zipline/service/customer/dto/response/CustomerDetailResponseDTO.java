package com.zipline.service.customer.dto.response;

import java.math.BigInteger;

import com.zipline.entity.customer.Customer;

import lombok.Getter;

@Getter
public class CustomerDetailResponseDTO {
	private Long uid;
	private String name;
	private String phoneNo;
	private String telProvider;
	private String legalDistrictCode;
	private BigInteger minRent;
	private BigInteger maxRent;
	private String trafficSource;
	private boolean isLandlord;
	private boolean isTenant;
	private boolean isBuyer;
	private boolean isSeller;
	private BigInteger maxPrice;
	private BigInteger minPrice;
	private BigInteger minDeposit;
	private BigInteger maxDeposit;
	private String birthDay;

	public CustomerDetailResponseDTO(Customer customer) {
		this.uid = customer.getUid();
		this.name = customer.getName();
		this.phoneNo = customer.getPhoneNo();
		this.telProvider = customer.getTelProvider();
		this.legalDistrictCode = customer.getLegalDistrictCode();
		this.minRent = customer.getMinRent();
		this.maxRent = customer.getMaxRent();
		this.trafficSource = customer.getTrafficSource();
		this.isLandlord = customer.isLandlord();
		this.isTenant = customer.isTenant();
		this.isBuyer = customer.isBuyer();
		this.isSeller = customer.isSeller();
		this.maxPrice = customer.getMaxPrice();
		this.minPrice = customer.getMinPrice();
		this.minDeposit = customer.getMinDeposit();
		this.maxDeposit = customer.getMaxDeposit();
		this.birthDay = customer.getBirthday();
	}
}
