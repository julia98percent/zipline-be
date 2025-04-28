package com.zipline.global.request;

import java.math.BigInteger;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgentPropertyFilterRequestDTO {
	private String legalDistrictCode;

	private String type;

	private String category;

	private BigInteger minDeposit;
	private BigInteger maxDeposit;

	private BigInteger minMonthlyRent;
	private BigInteger maxMonthlyRent;

	private BigInteger minPrice;
	private BigInteger maxPrice;

	private LocalDate minMoveInDate;
	private LocalDate maxMoveInDate;

	private Boolean petsAllowed;

	private Integer minFloor;
	private Integer maxFloor;

	private Boolean hasElevator;

	private Integer minConstructionYear;
	private Integer maxConstructionYear;

	private Integer minParkingCapacity;
	private Integer maxParkingCapacity;

	private Double minNetArea;
	private Double maxNetArea;

	private Double minTotalArea;
	private Double maxTotalArea;
}
