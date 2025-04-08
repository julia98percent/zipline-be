package com.zipline.dto;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;

import lombok.Getter;

@Getter
public class AgentPropertyListResponseDTO {
	private List<PropertyResponseDTO> agentProperty;
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;

	public AgentPropertyListResponseDTO(List<PropertyResponseDTO> agentProperty, Page page) {
		this.agentProperty = agentProperty;
		this.page = page.getNumber() + 1;
		this.size = page.getSize();
		this.totalElements = (int)page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.hasNext = page.hasNext();
	}

	@Getter
	public static class PropertyResponseDTO {
		private Long uid;
		private String customerName;
		private String address;
		private BigInteger deposit;
		private BigInteger monthlyRent;
		private BigInteger price;
		private PropertyType type;
		private LocalDate moveInDate;
		private PropertyCategory realCategory;
		private Boolean petsAllowed;
		private Integer floor;
		private Boolean hasElevator;
		private Year constructionYear;
		private Integer parkingCapacity;
		private Double netArea;
		private Double totalArea;
		private String details;

		public PropertyResponseDTO(AgentProperty agentProperty) {
			this.uid = agentProperty.getUid();
			this.customerName = agentProperty.getCustomer().getName();
			this.address = agentProperty.getAddress();
			this.deposit = agentProperty.getDeposit();
			this.monthlyRent = agentProperty.getMonthlyRent();
			this.price = agentProperty.getPrice();
			this.type = agentProperty.getType();
			this.moveInDate = agentProperty.getMoveInDate();
			this.realCategory = agentProperty.getRealCategory();
			this.petsAllowed = agentProperty.getPetsAllowed();
			this.floor = agentProperty.getFloor();
			this.hasElevator = agentProperty.getHasElevator();
			this.constructionYear = agentProperty.getConstructionYear();
			this.parkingCapacity = agentProperty.getParkingCapacity();
			this.netArea = agentProperty.getNetArea();
			this.totalArea = agentProperty.getTotalArea();
			this.details = agentProperty.getDetails();
		}

	}
}
