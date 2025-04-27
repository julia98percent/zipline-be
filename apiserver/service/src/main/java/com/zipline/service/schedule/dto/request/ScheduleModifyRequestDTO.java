package com.zipline.service.schedule.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleModifyRequestDTO {

  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String title;
  private String description;
  private Long customerUid;
}