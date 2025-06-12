package com.zipline.service.message.dto.response;

import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageHistoryResponseDTO {

  private String startKey;
  private String nextKey;
  private int limit;
  private Map<String, MessageHistoryDetailDTO> groupList;

  public static MessageHistoryResponseDTO emptyResponse() {
    return MessageHistoryResponseDTO.builder()
        .groupList(Collections.emptyMap())
        .build();
  }
}