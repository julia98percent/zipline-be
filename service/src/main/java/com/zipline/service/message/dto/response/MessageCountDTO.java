package com.zipline.service.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class MessageCountDTO {

  private int total;
  private int sentTotal;
  private int sentFailed;
  private int sentSuccess;
  private int sentPending;
  private int sentReplacement;
  private int refund;
  private int registeredFailed;
  private int registeredSuccess;
}