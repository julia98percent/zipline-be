package com.zipline.service.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequestDTO {
  private String from;
  private String to;
  private String text;

  public SendMessageRequestDTO withText(String text) {
    return new SendMessageRequestDTO(this.from, this.to, text);
  }
}