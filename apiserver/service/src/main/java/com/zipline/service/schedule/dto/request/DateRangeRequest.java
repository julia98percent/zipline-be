package com.zipline.service.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
public class DateRangeRequest {
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @NotNull(message = "시작 날짜는 필수입니다.")
  private LocalDateTime startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @NotNull(message = "종료 날짜는 필수입니다.")
  private LocalDateTime endDate;
}