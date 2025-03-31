package com.zipline.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "Customers")
@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid")
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "phone_no", nullable = false)
	private String phoneNo;

	@Column(name = "address")
	private String address;

	@Column(name = "tel_provider")
	private String telProvider;

	@Column(name = "region")
	private String region;

	@Column(name = "min_rent")
	private BigInteger minRent;

	@Column(name = "max_rent")
	private BigInteger maxRent;

	@Column(name = "traffic_source")
	private String trafficSource;

	@Column(name = "is_tenant", nullable = false)
	private boolean isTenant;

	@Column(name = "is_landlord", nullable = false)
	private boolean isLandlord;

	@Column(name = "is_buyer", nullable = false)
	private boolean isBuyer;

	@Column(name = "is_seller", nullable = false)
	private boolean isSeller;

	@Column(name = "max_price")
	private BigInteger maxPrice;

	@Column(name = "min_price")
	private BigInteger minPrice;

	@Column(name = "min_deposit")
	private BigInteger minDeposit;

	@Column(name = "max_deposit")
	private BigInteger maxDeposit;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	private Customer(User user, String name, String phoneNo, String address, String telProvider, String region,
		BigInteger minRent, BigInteger maxRent, String trafficSource, boolean isTenant, boolean isLandlord,
		boolean isBuyer,
		boolean isSeller, BigInteger maxPrice, BigInteger minPrice, BigInteger minDeposit, BigInteger maxDeposit,
		boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		this.user = user;
		this.name = name;
		this.phoneNo = phoneNo;
		this.address = address;
		this.telProvider = telProvider;
		this.region = region;
		this.minRent = minRent;
		this.maxRent = maxRent;
		this.trafficSource = trafficSource;
		this.isTenant = isTenant;
		this.isLandlord = isLandlord;
		this.isBuyer = isBuyer;
		this.isSeller = isSeller;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.minDeposit = minDeposit;
		this.maxDeposit = maxDeposit;
		this.isDeleted = isDeleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}
}
