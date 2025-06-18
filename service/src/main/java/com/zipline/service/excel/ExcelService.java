package com.zipline.service.excel;

import io.micrometer.core.annotation.Timed;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

  @Timed
  Map<String, Integer> registerCustomerByExcel(MultipartFile file, Long userUid);

  @Timed
  Map<String, Integer> registerPropertiesByExcel(MultipartFile multipartFile, Long userUid);
}