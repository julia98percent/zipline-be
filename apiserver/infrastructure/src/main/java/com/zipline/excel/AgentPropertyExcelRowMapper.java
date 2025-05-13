package com.zipline.excel;

import static com.zipline.global.util.ExcelParser.*;

import java.math.BigInteger;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.zipline.global.exception.excel.ExcelException;
import com.zipline.global.exception.excel.errorcode.ExcelErrorCode;

@Component
public class AgentPropertyExcelRowMapper implements ExcelRowMapper<AgentPropertyExcelDTO> {

	@Override
	public AgentPropertyExcelDTO map(Row row, DataFormatter formatter) {
		Integer rowNum = row.getRowNum();
		String customerName = getCell(row, 0, formatter);
		String phoneNo = formatPhone(getCell(row, 1, formatter));
		String roadName = getCell(row, 2, formatter);
		String detailAddress = getCell(row, 3, formatter);
		String realCategory = convertRealCategoryFromKorean(rowNum, getCell(row, 4, formatter));
		String type = convertPropertyTypeFromKorean(rowNum, getCell(row, 5, formatter));
		BigInteger deposit = parseBigInt(getCell(row, 6, formatter));
		BigInteger monthlyRent = parseBigInt(getCell(row, 7, formatter));
		BigInteger price = parseBigInt(getCell(row, 8, formatter));
		LocalDate startDate = parseDate(rowNum, "계약 시작일", getCell(row, 9, formatter));
		LocalDate endDate = parseDate(rowNum, "계약 종료일", getCell(row, 10, formatter));
		LocalDate moveInDate = parseDate(rowNum, "입주 가능일", getCell(row, 11, formatter));
		Boolean petsAllowed = parseReferneceBoolean(getCell(row, 12, formatter));
		Integer floor = parseInteger(getCell(row, 13, formatter));
		Boolean hasElevator = parseReferneceBoolean(getCell(row, 14, formatter));
		String constructionYear = getCell(row, 15, formatter);
		Double parkingCapacity = parseDouble(getCell(row, 16, formatter));
		Double netArea = parseDouble(getCell(row, 17, formatter));
		Double totalArea = parseDouble(getCell(row, 18, formatter));
		String details = getCell(row, 19, formatter);

		AgentPropertyExcelDTO dto = new AgentPropertyExcelDTO(rowNum, customerName, phoneNo, roadName, detailAddress,
			realCategory, type, deposit, monthlyRent, price, startDate, endDate, moveInDate, petsAllowed, floor,
			hasElevator, constructionYear, parkingCapacity, netArea, totalArea, details
		);
		dto.validate();
		return dto;
	}

	private String convertPropertyTypeFromKorean(int rowNum, String type) {
		switch (type) {
			case "매매":
				return "SALE";
			case "전세":
				return "DEPOSIT";
			case "월세":
				return "MONTHLY";
			default:
				throw new ExcelException(ExcelErrorCode.INVALID_INPUT_VALUE, rowNum, "Property Type", type,
					"잘못된 매물 유형입니다.");
		}
	}

	private String convertRealCategoryFromKorean(int rowNum, String realCategory) {
		switch (realCategory) {
			case "원룸":
				return "ONE_ROOM";
			case "투룸":
				return "TWO_ROOM";
			case "아파트":
				return "APARTMENT";
			case "빌라":
				return "VILLA";
			case "주택":
				return "HOUSE";
			case "오피스텔":
				return "OFFICETEL";
			case "상가":
				return "COMMERCIAL";
			default:
				throw new ExcelException(ExcelErrorCode.INVALID_INPUT_VALUE, rowNum, "Property Category", realCategory,
					"잘못된 매물 카테고리입니다.");
		}
	}
}
