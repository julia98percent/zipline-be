package com.zipline.service.agentProperty.dto.request;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.PropertyCategory;
import com.zipline.entity.enums.PropertyType;
import com.zipline.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AgentPropertyRequestDTO {

	@NotNull
	@Schema(description = "등록할 고객의 UID", example = "1", required = true)
	private Long customerUid;

	@Schema(description = "전체 주소", example = "서울특별시 강남구 삼성로85길 12", required = true)
	@NotBlank
	@Size(min = 1, max = 255)
	private String address;

	@Schema(description = "법정동 코드", example = "1100000000", required = true)
	@NotBlank
	@Size(min = 1, max = 20)
	private String legalDistrictCode;

	@Schema(description = "보증금", example = "50000000")
	@PositiveOrZero
	private BigInteger deposit;

	@Schema(description = "월세", example = "1000000")
	@PositiveOrZero
	private BigInteger monthlyRent;

	@Schema(description = "매매 가격", example = "800000000")
	@PositiveOrZero
	private BigInteger price;

	@Schema(description = "매물 유형", example = "SALE", required = true)
	@NotNull
	private PropertyType type;

	@Schema(description = "경도", example = "127.056503")
	private Double longitude;

	@Schema(description = "위도", example = "37.508742")
	private Double latitude;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 시작일", example = "2025-04-05")
	private LocalDate startDate;

	@Schema(description = "계약 종료일", example = "2026-04-05")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	@Schema(description = "입주 가능일", example = "2025-04-10")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate moveInDate;

	@Schema(description = "부동산 카테고리", example = "APARTMENT")
	private PropertyCategory realCategory;

	@NotNull
	@Schema(description = "반려동물 가능 여부", example = "true", required = true)
	private Boolean petsAllowed;

	@Schema(description = "층수", example = "5")
	@PositiveOrZero
	private Integer floor;

	@NotNull
	@Schema(description = "엘리베이터 유무", example = "true", required = true)
	private Boolean hasElevator;

	@Schema(description = "건축 연도", example = "2015")
	private Year constructionYear;

	@PositiveOrZero
	@Schema(description = "주차 가능 대수", example = "2")
	private Integer parkingCapacity;

	@NotNull
	@Positive
	@Schema(description = "전용 면적 (m²)", example = "18.5", required = true)
	private Double netArea;

	@NotNull
	@Positive
	@Schema(description = "공급 면적 (m²)", example = "25.0", required = true)
	private Double totalArea;

	@Schema(description = "기타 상세 사항", example = "풀옵션, 관리비 별도")
	@Size(max = 255)
	private String details;

	public AgentProperty toEntity(User user, Customer customer) {
		return AgentProperty.builder()
			.user(user)
			.customer(customer)
			.address(address)
			.legalDistrictCode(legalDistrictCode)
			.deposit(deposit)
			.monthlyRent(monthlyRent)
			.price(price)
			.type(type)
			.longitude(longitude)
			.latitude(latitude)
			.startDate(startDate)
			.endDate(endDate)
			.moveInDate(moveInDate)
			.realCategory(realCategory)
			.petsAllowed(petsAllowed)
			.floor(floor)
			.hasElevator(hasElevator)
			.constructionYear(constructionYear)
			.parkingCapacity(parkingCapacity)
			.netArea(netArea)
			.totalArea(totalArea)
			.details(details)
			.build();
	}
}
