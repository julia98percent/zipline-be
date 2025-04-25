package com.zipline.service.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageHistoryRequestDTO {

  @Schema(description = "검색 조건 필드명", example = "statusCode,type")
  private String criteria;

  @Schema(description = "검색 조건 연산자", example = "eq,eq")
  private String cond;

  @Schema(description = "검색 값", example = "2000,SMS")
  private String value;

}