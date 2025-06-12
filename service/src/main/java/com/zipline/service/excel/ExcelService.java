package com.zipline.service.excel;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

	Map<String, Integer> registerCustomerByExcel(MultipartFile file, Long userUid);

	Map<String, Integer> registerPropertiesByExcel(MultipartFile multipartFile, Long userUid);
}
