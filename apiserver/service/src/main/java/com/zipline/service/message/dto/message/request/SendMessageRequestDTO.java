package com.zipline.service.message.dto.message.request;

import lombok.Getter;

@Getter
public class SendMessageRequestDTO {
  private String from;
  private String to;
  private String text;
}