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

	@NotNull(message = "등록할 고객을 선택해주세요.")
	@Schema(description = "등록할 고객의 UID", example = "1", required = true)
	private Long customerUid;

	@NotBlank(message = "주소를 입력해주세요.")
	@Size(min = 1, max = 255, message = "주소는 1자 이상 255자 이하로 입력해주세요.")
	@Schema(description = "전체 주소", example = "서울특별시 강남구 삼성로85길 12", required = true)
	private String address;

	@NotBlank
	@Size(min = 1, max = 20)
	@Schema(description = "법정동 코드", example = "1100000000", required = true)
	private String legalDistrictCode;

	@PositiveOrZero(message = "보증금은 0 이상의 값이어야 합니다.")
	@Schema(description = "보증금", example = "50000000")
	private BigInteger deposit;

	@PositiveOrZero(message = "월세는 0 이상의 값이어야 합니다.")
	@Schema(description = "월세", example = "1000000")
	private BigInteger monthlyRent;

	@PositiveOrZero(message = "매매 가격은 0 이상의 값이어야 합니다.")
	@Schema(description = "매매 가격", example = "800000000")
	private BigInteger price;

	@NotNull(message = "매물 유형을 선택해주세요.")
	@Schema(description = "매물 유형", example = "SALE", required = true)
	private PropertyType type;

	@Schema(description = "경도", example = "127.056503")
	private Double longitude;

	@Schema(description = "위도", example = "37.508742")
	private Double latitude;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 시작일", example = "2025-04-05")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "계약 종료일", example = "2026-04-05")
	private LocalDate endDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "입주 가능일", example = "2025-04-10")
	private LocalDate moveInDate;

	@Schema(description = "부동산 카테고리", example = "APARTMENT")
	private PropertyCategory realCategory;

	@NotNull(message = "반려동물 가능 여부를 선택해주세요.")
	@Schema(description = "반려동물 가능 여부", example = "true", required = true)
	private Boolean petsAllowed;

	@PositiveOrZero(message = "층수는 0 이상의 값이어야 합니다.")
	@Schema(description = "층수", example = "5")
	private Integer floor;

	@NotNull(message = "엘리베이터 유무를 선택해주세요.")
	@Schema(description = "엘리베이터 유무", example = "true", required = true)
	private Boolean hasElevator;

	@Schema(description = "건축 연도", example = "2015")
	private Year constructionYear;

	@PositiveOrZero(message = "주차 가능 대수는 0 이상의 값이어야 합니다.")
	@Schema(description = "주차 가능 대수", example = "2")
	private Integer parkingCapacity;

	@NotNull(message = "전용 면적을 입력해주세요.")
	@Positive(message = "전용 면적은 0보다 커야 합니다.")
	@Schema(description = "전용 면적 (m²)", example = "18.5", required = true)
	private Double netArea;

	@NotNull(message = "공급 면적을 입력해주세요.")
	@Positive(message = "공급 면적은 0보다 커야 합니다.")
	@Schema(description = "공급 면적 (m²)", example = "25.0", required = true)
	private Double totalArea;

	@Size(max = 255, message = "상세 정보는 255자 이내로 입력해주세요.")
	@Schema(description = "기타 상세 사항", example = "풀옵션, 관리비 별도")
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
