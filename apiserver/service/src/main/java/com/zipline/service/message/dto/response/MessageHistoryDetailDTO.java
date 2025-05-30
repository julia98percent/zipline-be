package com.zipline.service.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class MessageHistoryDetailDTO {

  private String groupId;
  private String from;
  private String type;
  private String subject;
  private String dateCreated;
  private String dateSent;
  private String dateCompleted;
  private String dateUpdated;
  private String statusCode;
  private String status;
  private String to;
  private String text;
  private String messageId;
  private MessageCountDTO count;
  private List<MessageLogDTO> log;
}