package com.zipline.service.agentProperty.dto.response;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgentPropertyResponseDTO {

	private String customer;
	private String address;
	private String legalDistrictCode;
	private String detailAddress;
	private BigInteger deposit;
	private BigInteger monthlyRent;
	private BigInteger price;
	private PropertyType type;
	private Double longitude;
	private Double latitude;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate moveInDate;
	private PropertyCategory realCategory;
	private Boolean petsAllowed;
	private Integer floor;
	private Boolean hasElevator;
	private Year constructionYear;
	private Double parkingCapacity;
	private Double netArea;
	private Double totalArea;
	private String details;

	public static AgentPropertyResponseDTO of(AgentProperty property) {
		return AgentPropertyResponseDTO.builder()
			.customer(property.getCustomer().getName())
			.address(property.getAddress())
			.legalDistrictCode(property.getLegalDistrictCode())
			.detailAddress(property.getDetailAddress())
			.deposit(property.getDeposit())
			.monthlyRent(property.getMonthlyRent())
			.price(property.getPrice())
			.type(property.getType())
			.longitude(property.getLongitude())
			.latitude(property.getLatitude())
			.startDate(property.getStartDate())
			.endDate(property.getEndDate())
			.moveInDate(property.getMoveInDate())
			.realCategory(property.getRealCategory())
			.petsAllowed(property.getPetsAllowed())
			.floor(property.getFloor())
			.hasElevator(property.getHasElevator())
			.constructionYear(property.getConstructionYear())
			.parkingCapacity(property.getParkingCapacity())
			.netArea(property.getNetArea())
			.totalArea(property.getTotalArea())
			.details(property.getDetails())
			.build();
	}
}