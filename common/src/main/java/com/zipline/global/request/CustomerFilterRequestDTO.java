package com.zipline.global.request;

import java.math.BigInteger;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CustomerFilterRequestDTO {

	private String search;
	private String regionCode;
	private Boolean tenant;
	private Boolean landlord;
	private Boolean buyer;
	private Boolean seller;
	private Boolean noRole;
	private BigInteger minPrice;
	private BigInteger maxPrice;
	private BigInteger minDeposit;
	private BigInteger maxDeposit;
	private BigInteger minRent;
	private BigInteger maxRent;
	private List<Long> labelUids;
}