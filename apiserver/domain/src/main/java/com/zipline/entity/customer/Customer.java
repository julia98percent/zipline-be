package com.zipline.entity.customer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.label.LabelCustomer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customers", uniqueConstraints = {
	@UniqueConstraint(name = "name_phone_unique", columnNames = {"name", "phone_no"})})
@Entity
public class Customer extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid")
	private User user;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "phone_no", length = 13, nullable = false)
	private String phoneNo;

	@Column(name = "legal_district_code", length = 10)
	private String legalDistrictCode;

	@Column(name = "tel_provider")
	private String telProvider;

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

	@Column(name = "birth_day", length = 8)
	private String birthday;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LabelCustomer> labelCustomers = new ArrayList<>();

	@Builder
	private Customer(User user, String name, String phoneNo, String telProvider,
		String legalDistrictCode,
		BigInteger minRent, BigInteger maxRent, String trafficSource, boolean isTenant, boolean isLandlord,
		boolean isBuyer,
		boolean isSeller, BigInteger maxPrice, BigInteger minPrice, BigInteger minDeposit, BigInteger maxDeposit,
		String birthday) {
		this.user = user;
		this.name = name;
		this.phoneNo = phoneNo;
		this.telProvider = telProvider;
		this.legalDistrictCode = legalDistrictCode;
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
		this.birthday = birthday;
	}

	public void modifyCustomer(String name, String phoneNo, String telProvider,
		String legalDistrictCode,
		BigInteger minRent, BigInteger maxRent, String trafficSource, boolean isTenant, boolean isLandlord,
		boolean isBuyer, boolean isSeller, BigInteger maxPrice, BigInteger minPrice, BigInteger minDeposit,
		BigInteger maxDeposit, String birthday) {
		validatePrices(minRent, maxRent, minPrice, maxPrice, minDeposit, maxDeposit);
		this.name = name;
		this.phoneNo = phoneNo;
		this.telProvider = telProvider;
		this.legalDistrictCode = legalDistrictCode;
		this.minRent = minRent;
		this.maxRent = maxRent;
		this.trafficSource = trafficSource;
		this.isTenant = isTenant;
		this.isLandlord = isLandlord;
		this.isBuyer = isBuyer;
		this.isSeller = isSeller;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.minDeposit = minDeposit;
		this.maxDeposit = maxDeposit;
		this.birthday = birthday;
	}

	private void validatePrices(BigInteger minRent, BigInteger maxRent, BigInteger minPrice, BigInteger maxPrice,
		BigInteger minDeposit, BigInteger maxDeposit) {
		validateNotNegative(minRent);
		validateNotNegative(maxRent);
		validateNotNegative(minPrice);
		validateNotNegative(maxPrice);
		validateNotNegative(minDeposit);
		validateNotNegative(maxDeposit);
		validateRange(minRent, maxRent);
		validateRange(minPrice, maxPrice);
		validateRange(minDeposit, maxDeposit);
	}

	private void validateNotNegative(BigInteger price) {
		if (price == null) {
			return;
		}

		if (price.compareTo(BigInteger.ZERO) < 0) {
			throw new CustomerException(CustomerErrorCode.NEGATIVE_PRICE);
		}
	}

	private void validateRange(BigInteger min, BigInteger max) {
		if (min == null || max == null) {
			return;
		}

		if (min.compareTo(max) > 0) {
			throw new CustomerException(CustomerErrorCode.INVALID_PRICE_RANGE);
		}
	}
}
