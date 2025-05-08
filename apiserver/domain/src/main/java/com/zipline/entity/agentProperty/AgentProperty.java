package com.zipline.entity.agentProperty;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;
import com.zipline.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "agent_properties")
public class AgentProperty extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_uid", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid", nullable = false)
	private User user;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "legal_district_code", length = 10, nullable = false)
	private String legalDistrictCode;

	@Column(name = "detail_address")
	private String detailAddress;

	@Column(name = "deposit")
	private BigInteger deposit;

	@Column(name = "monthly_rent")
	private BigInteger monthlyRent;

	@Column(name = "price")
	private BigInteger price;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private PropertyType type;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "move_in_date")
	private LocalDate moveInDate;

	@Column(name = "real_category", nullable = false)
	@Enumerated(EnumType.STRING)
	private PropertyCategory realCategory;

	@Column(name = "pets_allowed", nullable = false)
	private Boolean petsAllowed;

	@Column(name = "floor")
	private Integer floor;

	@Column(name = "has_elevator", nullable = false)
	private Boolean hasElevator;

	@Column(name = "construction_year")
	private Year constructionYear;

	@Column(name = "parking_capacity")
	private Integer parkingCapacity;

	@Column(name = "net_area", nullable = false)
	private Double netArea;

	@Column(name = "total_area", nullable = false)
	private Double totalArea;

	@Column(name = "details", length = 255)
	private String details;

	@Builder
	private AgentProperty(Customer customer, User user, String address, String legalDistrictCode, String detailAddress,
		BigInteger deposit,
		BigInteger monthlyRent, BigInteger price, PropertyType type, Double longitude, Double latitude,
		LocalDate startDate,
		LocalDate endDate, LocalDate moveInDate, PropertyCategory realCategory, Boolean petsAllowed, Integer floor,
		Boolean hasElevator, Year constructionYear, Integer parkingCapacity, Double netArea, Double totalArea,
		String details) {
		this.customer = customer;
		this.user = user;
		this.address = address;
		this.legalDistrictCode = legalDistrictCode;
		this.detailAddress = detailAddress;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
		this.price = price;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.startDate = startDate;
		this.endDate = endDate;
		this.moveInDate = moveInDate;
		this.realCategory = realCategory;
		this.petsAllowed = petsAllowed;
		this.floor = floor;
		this.hasElevator = hasElevator;
		this.constructionYear = constructionYear;
		this.parkingCapacity = parkingCapacity;
		this.netArea = netArea;
		this.totalArea = totalArea;
		this.details = details;
	}

	public void modifyProperty(Customer customer, String address, String legalDistrictCode, String detailAddress,
		BigInteger deposit,
		BigInteger monthlyRent, BigInteger price, PropertyType type, Double longitude, Double latitude,
		LocalDate startDate,
		LocalDate endDate, LocalDate moveInDate, PropertyCategory realCategory, Boolean petsAllowed, Integer floor,
		Boolean hasElevator, Year constructionYear, Integer parkingCapacity, Double netArea, Double totalArea,
		String details) {
		this.customer = customer;
		this.address = address;
		this.detailAddress = detailAddress;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
		this.price = price;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.legalDistrictCode = legalDistrictCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.moveInDate = moveInDate;
		this.realCategory = realCategory;
		this.petsAllowed = petsAllowed;
		this.floor = floor;
		this.hasElevator = hasElevator;
		this.constructionYear = constructionYear;
		this.parkingCapacity = parkingCapacity;
		this.netArea = netArea;
		this.totalArea = totalArea;
		this.details = details;
	}
}