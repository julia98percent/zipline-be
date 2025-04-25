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

  @Schema(description = "현재 목록을 불러올 기준이 되는 키", example = "specific group id")
  private String startKey;

  @Schema(description = "한 페이지에 불러옥 목록 개수", example = "10")
  private Integer limit;

}