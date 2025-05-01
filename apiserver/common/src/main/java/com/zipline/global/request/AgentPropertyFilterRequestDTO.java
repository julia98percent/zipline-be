package com.zipline.global.request;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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

	@DecimalMin(value = "0", message = "보증금은 0 이상이어야 합니다.")
	private BigInteger minDeposit;

	@DecimalMin(value = "0", message = "보증금은 0 이상이어야 합니다.")
	private BigInteger maxDeposit;

	@DecimalMin(value = "0", message = "월세는 0 이상이어야 합니다.")
	private BigInteger minMonthlyRent;

	@DecimalMin(value = "0", message = "월세는 0 이상이어야 합니다.")
	private BigInteger maxMonthlyRent;

	@DecimalMin(value = "0", message = "매매가는 0 이상이어야 합니다.")
	private BigInteger minPrice;

	@DecimalMin(value = "0", message = "매매가는 0 이상이어야 합니다.")
	private BigInteger maxPrice;

	private LocalDate minMoveInDate;
	private LocalDate maxMoveInDate;

	private Boolean petsAllowed;

	private Integer minFloor;
	private Integer maxFloor;

	private Boolean hasElevator;

	private Integer minConstructionYear;
	private Integer maxConstructionYear;

	@Min(value = 0, message = "주차 가능 대수는 0 이상이어야 합니다.")
	private Integer minParkingCapacity;

	@Min(value = 0, message = "주차 가능 대수는 0 이상이어야 합니다.")
	private Integer maxParkingCapacity;

	@DecimalMin(value = "0.0", inclusive = true, message = "전용면적은 0 이상이어야 합니다.")
	private Double minNetArea;

	@DecimalMin(value = "0.0", inclusive = true, message = "전용면적은 0 이상이어야 합니다.")
	private Double maxNetArea;

	@DecimalMin(value = "0.0", inclusive = true, message = "공급면적은 0 이상이어야 합니다.")
	private Double minTotalArea;

	@DecimalMin(value = "0.0", inclusive = true, message = "공급면적은 0 이상이어야 합니다.")
	private Double maxTotalArea;

	public void validate() {
		if (minDeposit != null && minDeposit.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("보증금은 0 이상이어야 합니다.");
		}
		if (maxDeposit != null && maxDeposit.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("보증금은 0 이상이어야 합니다.");
		}
		if (minMonthlyRent != null && minMonthlyRent.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("월세는 0 이상이어야 합니다.");
		}
		if (maxMonthlyRent != null && maxMonthlyRent.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("월세는 0 이상이어야 합니다.");
		}
		if (minPrice != null && minPrice.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("매매가는 0 이상이어야 합니다.");
		}
		if (maxPrice != null && maxPrice.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException("매매가는 0 이상이어야 합니다.");
		}

		if (minDeposit != null && maxDeposit != null && minDeposit.compareTo(maxDeposit) > 0) {
			throw new IllegalArgumentException("보증금 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minMonthlyRent != null && maxMonthlyRent != null && minMonthlyRent.compareTo(maxMonthlyRent) > 0) {
			throw new IllegalArgumentException("월세 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
			throw new IllegalArgumentException("매매가 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minMoveInDate != null && maxMoveInDate != null && minMoveInDate.isAfter(maxMoveInDate)) {
			throw new IllegalArgumentException("입주 가능일 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minFloor != null && maxFloor != null && minFloor > maxFloor) {
			throw new IllegalArgumentException("층수 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minConstructionYear != null && maxConstructionYear != null && minConstructionYear > maxConstructionYear) {
			throw new IllegalArgumentException("준공연도 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minParkingCapacity != null && maxParkingCapacity != null && minParkingCapacity > maxParkingCapacity) {
			throw new IllegalArgumentException("주차 가능 대수 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minNetArea != null && maxNetArea != null && minNetArea > maxNetArea) {
			throw new IllegalArgumentException("전용면적 최소값이 최대값보다 클 수 없습니다.");
		}
		if (minTotalArea != null && maxTotalArea != null && minTotalArea > maxTotalArea) {
			throw new IllegalArgumentException("공급면적 최소값이 최대값보다 클 수 없습니다.");
		}
	}
}
