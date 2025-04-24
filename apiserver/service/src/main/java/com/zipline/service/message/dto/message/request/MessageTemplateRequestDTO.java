package com.zipline.service.message.dto.message.request;

import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.entity.message.MessageTemplate;
import lombok.Getter;

@Getter
public class MessageTemplateRequestDTO {
  private String name;
  private MessageTemplateCategory category;
  private String content;

  public MessageTemplate toEntity(String name, MessageTemplateCategory category, String content) {
    return MessageTemplate.builder()
        .name(name)
        .category(category)
        .content(content)
        .build();
  }
}