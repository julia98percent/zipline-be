package com.zipline.service.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateRangeRequest {
  @NotNull(message = "시작 날짜는 필수입니다.")
  private LocalDateTime startDate;

  @NotNull(message = "종료 날짜는 필수입니다.")
  private LocalDateTime endDate;
}