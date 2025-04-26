package com.zipline.service.message.dto.response;

import lombok.Getter;


@Getter
public class MessageHistoryDetailDTO {
  private String groupId;
  private String from;
  private String type;
  private String subject;
  private String dateCreated;
  private String dateUpdated;
  private String statusCode;
  private String status;
  private String to;
  private String text;
  private String messageId;
}