package com.zipline.excel;

import java.math.BigInteger;
import java.time.LocalDate;

import org.springframework.util.StringUtils;

import com.zipline.global.exception.excel.ExcelException;
import com.zipline.global.exception.excel.errorcode.ExcelErrorCode;

import lombok.Getter;

@Getter
public class AgentPropertyExcelDTO {
	private Integer rowNum;
	private String customerName;
	private String phoneNo;
	private String roadName;
	private String detailAddress;
	private String realCategory;
	private String type;
	private BigInteger deposit;
	private BigInteger monthlyRent;
	private BigInteger price;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate moveInDate;
	private Boolean petsAllowed;
	private Integer floor;
	private Boolean hasElevator;
	private String constructionYear;
	private Double parkingCapacity;
	private Double netArea;
	private Double totalArea;
	private String details;

	public AgentPropertyExcelDTO(Integer rowNum, String customerName, String phoneNo, String roadName,
		String detailAddress,
		String realCategory, String type,
		BigInteger deposit, BigInteger monthlyRent, BigInteger price, LocalDate startDate, LocalDate endDate,
		LocalDate moveInDate, Boolean petsAllowed, Integer floor, Boolean hasElevator, String constructionYear,
		Double parkingCapacity, Double netArea, Double totalArea, String details) {
		this.rowNum = rowNum;
		this.customerName = customerName;
		this.phoneNo = phoneNo;
		this.roadName = roadName;
		this.detailAddress = detailAddress;
		this.realCategory = realCategory;
		this.type = type;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
		this.price = price;
		this.startDate = startDate;
		this.endDate = endDate;
		this.moveInDate = moveInDate;
		this.petsAllowed = petsAllowed;
		this.floor = floor;
		this.hasElevator = hasElevator;
		this.constructionYear = constructionYear;
		this.parkingCapacity = parkingCapacity;
		this.netArea = netArea;
		this.totalArea = totalArea;
		this.details = details;
	}

	public void validate() {
		if (!StringUtils.hasText(customerName)) {
			throwValidation("customerName", customerName, "이름은 필수입니다.");
		}
		if (!StringUtils.hasText(phoneNo) || !phoneNo.matches("^(\\d{3})-(\\d{3,4})-(\\d{4})$")) {
			throwValidation("phoneNo", phoneNo, "전화번호 형식이 올바르지 않습니다.");
		}
		if (!StringUtils.hasText(roadName)) {
			throwValidation("address", roadName, "주소는 필수입니다.");
		}
		if (!StringUtils.hasText(realCategory)) {
			throwValidation("realCategory", realCategory, "유형(카테고리)은 필수입니다.");
		}
		if (!StringUtils.hasText(type)) {
			throwValidation("type", type, "매물 타입은 필수입니다.");
		}
		if (netArea == null) {
			throwValidation("netArea", "null", "전용 면적은 필수입니다.");
		}
		if (totalArea == null) {
			throwValidation("totalArea", "null", "공급 면적은 필수입니다.");
		}
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throwValidation("startDate/endDate", startDate + "/" + endDate, "계약 시작일은 계약 종료일보다 미래일 수 없습니다.");
		}
		if (petsAllowed == null) {
			throwValidation("petsAllowed", "null", "반려동물 허용 여부는 필수입니다.");
		}
		if (hasElevator == null) {
			throwValidation("hasElevator", "null", "엘리베이터 여부는 필수입니다.");
		}
		if (StringUtils.hasText(details) && details.length() > 255) {
			throwValidation("details", details, "세부 사항의 최대 길이는 255자 입니다.");
		}
	}

	private void throwValidation(String field, Object value, String message) {
		throw new ExcelException(ExcelErrorCode.INVALID_INPUT_VALUE, rowNum, field, value, message);
	}
}
