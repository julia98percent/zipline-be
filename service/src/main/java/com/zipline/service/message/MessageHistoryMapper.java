package com.zipline.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.response.MessageListResponseDTO;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageHistoryMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static MessageHistoryResponseDTO mapToDTO(Map<String, Object> rawData) {
    return objectMapper.convertValue(rawData, MessageHistoryResponseDTO.class);
  }

  public static MessageListResponseDTO messageListMapToDTO(Map<String, Object> rawData) {
    return objectMapper.convertValue(rawData, MessageListResponseDTO.class);
  }
}