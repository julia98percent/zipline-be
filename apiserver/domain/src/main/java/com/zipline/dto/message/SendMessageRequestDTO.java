package com.zipline.dto.message;

import lombok.Getter;

@Getter
public class SendMessageRequestDTO {
  private String from;
  private String to;
  private String text;
}