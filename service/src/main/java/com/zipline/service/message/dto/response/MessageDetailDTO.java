package com.zipline.service.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDetailDTO {

  private String messageId;
  private String groupId;
  private String from;
  private String to;
  private String type;
  private String country;
  private String text;
  private String dateCreated;
  private String dateUpdated;
  private String dateReceived;
  private String status;
  private String statusCode;
  private String networkName;
  private List<MessageLogDTO> log;


}