package com.zipline.service.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageListResponseDTO {

  private String startKey;
  private String nextKey;
  private int limit;
  private Map<String, MessageDetailDTO> messageList;
}