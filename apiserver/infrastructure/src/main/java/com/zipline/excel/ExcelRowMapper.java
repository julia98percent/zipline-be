package com.zipline.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

public interface ExcelRowMapper<T> {
	T map(Row row, DataFormatter formatter);
}
