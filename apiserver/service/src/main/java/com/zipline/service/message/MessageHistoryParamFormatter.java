package com.zipline.service.message;


import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class MessageHistoryParamFormatter {
  public Map<String, String> formatQueryParams(MessageHistoryRequestDTO requestDTO) {
    Map<String, String> queryParams = new HashMap<>();

    if (StringUtils.hasText(requestDTO.getCriteria())) {
      queryParams.put("criteria", requestDTO.getCriteria());
    }

    if (StringUtils.hasText(requestDTO.getCond())) {
      queryParams.put("cond", requestDTO.getCond());
    }

    if (StringUtils.hasText(requestDTO.getValue())) {
      queryParams.put("value", requestDTO.getValue());
    }

    return queryParams;
  }
}