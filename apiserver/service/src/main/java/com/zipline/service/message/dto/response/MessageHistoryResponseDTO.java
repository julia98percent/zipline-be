package com.zipline.service.message.dto.response;

import java.util.Map;
import lombok.Getter;

@Getter
public class MessageHistoryResponseDTO {
  private String startKey;
  private int limit;
  private Map<String, MessageHistoryDetailDTO> messageList;
}