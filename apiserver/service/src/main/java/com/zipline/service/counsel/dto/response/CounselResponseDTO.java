package com.zipline.service.counsel.dto.response;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.counsel.CounselDetail;
import com.zipline.entity.customer.Customer;

import lombok.Getter;

@Getter
public class CounselResponseDTO {
	private Long counselUid;
	private String title;
	private String type;
	private LocalDateTime counselDate;
	private LocalDate dueDate;
	private Long propertyUid;
	private boolean completed;
	private List<CounselDetailInfo> counselDetails;
	private CustomerInfo customer;
	private PropertyInfo property;

	public CounselResponseDTO(Counsel counsel, List<CounselDetail> counselDetails, String preferredRegion) {
		this.counselUid = counsel.getUid();
		this.title = counsel.getTitle();
		this.type = counsel.getType().name().toString();
		this.counselDate = counsel.getCounselDate();
		this.dueDate = counsel.getDueDate();
		this.propertyUid = Optional.ofNullable(counsel.getAgentProperty())
			.map(AgentProperty::getUid)
			.orElse(null);
		this.completed = counsel.isCompleted();
		this.counselDetails = counselDetails.stream()
			.map(CounselDetailInfo::new)
			.collect(Collectors.toList());
		this.customer = new CustomerInfo(counsel.getCustomer(), preferredRegion);
		this.property = Optional.ofNullable(counsel.getAgentProperty())
			.map(PropertyInfo::new)
			.orElse(null);
	}

	@Getter
	public static class CounselDetailInfo {
		private Long counselDetailUid;
		private String question;
		private String answer;

		public CounselDetailInfo(CounselDetail counselDetail) {
			this.counselDetailUid = counselDetail.getUid();
			this.question = counselDetail.getQuestion();
			this.answer = counselDetail.getAnswer();
		}
	}

	@Getter
	public static class CustomerInfo {
		private Long uid;
		private String name;
		private String phoneNo;
		private boolean isLandlord;
		private boolean isTenant;
		private boolean isBuyer;
		private boolean isSeller;
		private String preferredRegion;
		private BigInteger minPrice;
		private BigInteger maxPrice;
		private BigInteger minDeposit;
		private BigInteger maxDeposit;
		private BigInteger minRent;
		private BigInteger maxRent;

		public CustomerInfo(Customer customer, String preferredRegion) {
			this.uid = customer.getUid();
			this.name = customer.getName();
			this.phoneNo = customer.getPhoneNo();
			this.preferredRegion = preferredRegion;
			this.isLandlord = customer.isLandlord();
			this.isTenant = customer.isTenant();
			this.isBuyer = customer.isBuyer();
			this.isSeller = customer.isSeller();
			this.minPrice = customer.getMinPrice();
			this.maxPrice = customer.getMaxPrice();
			this.minDeposit = customer.getMinDeposit();
			this.maxDeposit = customer.getMaxDeposit();
			this.minRent = customer.getMinRent();
			this.maxRent = customer.getMaxRent();
		}
	}

	@Getter
	public static class PropertyInfo {
		private String address;
		private String type;
		private BigInteger price;
		private BigInteger deposit;
		private BigInteger monthlyRent;
		private Double exclusiveArea;
		private Double supplyArea;
		private Integer floor;
		private Year constructionYear;
		private boolean hasElevator;
		private Integer parkingCapacity;
		private boolean hasPet;
		private String description;

		public PropertyInfo(AgentProperty property) {
			this.address = property.getAddress();
			this.type = property.getRealCategory().name().toString();
			this.price = property.getPrice();
			this.deposit = property.getDeposit();
			this.monthlyRent = property.getMonthlyRent();
			this.exclusiveArea = property.getTotalArea();
			this.supplyArea = property.getNetArea();
			this.floor = property.getFloor();
			this.constructionYear = property.getConstructionYear();
			this.hasElevator = property.getHasElevator();
			this.parkingCapacity = property.getParkingCapacity();
			this.hasPet = property.getPetsAllowed();
			this.description = property.getDetails();
		}
	}
}