package com.zipline.excel;

import static com.zipline.global.util.ExcelParser.*;

import java.math.BigInteger;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

@Component
public class CustomerExcelRowMapper implements ExcelRowMapper<CustomerExcelDTO> {

	@Override
	public CustomerExcelDTO map(Row row, DataFormatter formatter) {
		Integer rowNum = row.getRowNum();
		String name = getCell(row, 0, formatter);
		String phoneNo = formatPhone(getCell(row, 1, formatter));
		String telProvider = getCell(row, 2, formatter);
		String preferredRegion = getCell(row, 3, formatter);
		boolean landlord = parsePrimitiveBoolean(getCell(row, 4, formatter));
		boolean tenant = parsePrimitiveBoolean(getCell(row, 5, formatter));
		boolean buyer = parsePrimitiveBoolean(getCell(row, 6, formatter));
		boolean seller = parsePrimitiveBoolean(getCell(row, 7, formatter));
		BigInteger minRent = parseBigInt(getCell(row, 8, formatter));
		BigInteger maxRent = parseBigInt(getCell(row, 9, formatter));
		BigInteger minPrice = parseBigInt(getCell(row, 10, formatter));
		BigInteger maxPrice = parseBigInt(getCell(row, 11, formatter));
		BigInteger minDeposit = parseBigInt(getCell(row, 12, formatter));
		BigInteger maxDeposit = parseBigInt(getCell(row, 13, formatter));
		String birthDay = getCell(row, 14, formatter);

		CustomerExcelDTO dto = new CustomerExcelDTO(
			rowNum,
			name,
			phoneNo,
			telProvider,
			preferredRegion,
			landlord,
			tenant,
			buyer,
			seller,
			minRent,
			maxRent,
			maxPrice,
			minPrice,
			minDeposit,
			maxDeposit,
			birthDay
		);
		dto.validate();
		return dto;
	}
}
