package com.zipline.entity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import com.zipline.dto.AgentPropertyRequestDTO;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_properties")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AgentProperty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_uid", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid", nullable = false)
	private User user;

	@Column(nullable = false)
	private String address;

	@Column
	private String address1;

	@Column
	private String address2;

	@Column
	private String address3;

	@Column
	private BigInteger deposit;

	@Column(name = "monthly_rent")
	private BigInteger monthlyRent;

	@Column
	private BigInteger price;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PropertyType type;

	@Column
	private Double longitude;

	@Column
	private Double latitude;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "move_in_date")
	private LocalDate moveInDate;

	@Column(name = "real_category")
	@Enumerated(EnumType.STRING)
	private PropertyCategory realCategory;

	@Column(name = "pets_allowed", nullable = false)
	private Boolean petsAllowed;

	@Column
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

	@Column
	private String details;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void modifyProperty(AgentPropertyRequestDTO dto, User user, Boolean isDeleted,
		Customer customer, LocalDateTime createdAt,
		LocalDateTime updatedAt, LocalDateTime deletedAt) {

		this.user = user;
		this.customer = customer;
		this.address = dto.getAddress();
		this.address1 = dto.getDong();
		this.address2 = dto.getRoadName();
		this.address3 = dto.getExtraAddress();
		this.deposit = dto.getDeposit();
		this.monthlyRent = dto.getMonthlyRent();
		this.price = dto.getPrice();
		this.type = dto.getType();
		this.longitude = dto.getLongitude();
		this.latitude = dto.getLatitude();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.moveInDate = dto.getMoveInDate();
		this.realCategory = dto.getRealCategory();
		this.petsAllowed = dto.getPetsAllowed();
		this.floor = dto.getFloor();
		this.hasElevator = dto.getHasElevator();
		this.constructionYear = dto.getConstructionYear();
		this.parkingCapacity = dto.getParkingCapacity();
		this.netArea = dto.getNetArea();
		this.totalArea = dto.getTotalArea();
		this.details = dto.getDetails();
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public void delete(LocalDateTime deletedAt) {
		this.isDeleted = true;
		this.deletedAt = deletedAt;
	}
}