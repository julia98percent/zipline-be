package com.zipline.global.response;

import java.util.Collections;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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