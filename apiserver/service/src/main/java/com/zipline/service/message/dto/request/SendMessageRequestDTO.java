package com.zipline.service.message.dto.request;

import lombok.Getter;

@Getter
public class SendMessageRequestDTO {
  private String from;
  private String to;
  private String text;
}