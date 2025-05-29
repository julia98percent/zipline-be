package com.zipline.service.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class MessageLogDTO {

  private String createAt;
  private String message;
}