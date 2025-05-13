package com.zipline.global.util;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.zipline.global.exception.excel.ExcelException;
import com.zipline.global.exception.excel.errorcode.ExcelErrorCode;

@Component
public class ExcelParser {

	private ExcelParser() {
	}

	public static String formatPhone(String value) {
		if (value == null)
			return null;
		String digits = value.replaceAll("\\D", ""); // 숫자만 추출

		if (digits.length() == 11) {
			return digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
		} else if (digits.length() == 10) {
			return digits.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
		} else {
			return value;
		}
	}

	public static String getCell(Row row, int idx, DataFormatter formatter) {
		Cell cell = row.getCell(idx);
		if (cell == null)
			return null;
		String value = formatter.formatCellValue(cell);
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	public static BigInteger parseBigInt(String value) {
		if (!StringUtils.hasText(value))
			return null;
		return new BigInteger(value.replaceAll(",", ""));
	}

	public static boolean parsePrimitiveBoolean(String value) {
		return "Y".equalsIgnoreCase(value) || "TRUE".equalsIgnoreCase(value) || "O".equalsIgnoreCase(value);
	}

	public static Boolean parseReferneceBoolean(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		if ("Y".equalsIgnoreCase(value) || "TRUE".equalsIgnoreCase(value) || "O".equalsIgnoreCase(value)) {
			return true;
		}

		if ("N".equalsIgnoreCase(value) || "NO".equalsIgnoreCase(value) || "FALSE".equalsIgnoreCase(value)) {
			return false;
		}
		return null;
	}

	public static LocalDate parseDate(int rowNum, String filedName, String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		try {
			return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (DateTimeParseException e) {
			throw new ExcelException(ExcelErrorCode.INVALID_INPUT_VALUE, rowNum, filedName, value,
				"날짜는 YYYY-MM-DD 형식이어야 합니다.");
		}
	}

	public static Integer parseInteger(String value) {
		if (!StringUtils.hasText(value))
			return null;
		return Integer.parseInt(value.replaceAll("\\D", ""));
	}

	public static Double parseDouble(String value) {
		if (!StringUtils.hasText(value))
			return null;
		return Double.parseDouble(value.replaceAll(",", ""));
	}
}
