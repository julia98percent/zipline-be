package com.zipline.service.schedule.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleModifyRequestDTO {

  @NotNull(message = "시작 날짜는 필수 입력값입니다.")
  private LocalDateTime startDate;

  @NotNull(message = "종료 날짜는 필수 입력값입니다.")
  private LocalDateTime endDate;

  @NotBlank(message = "제목은 필수 입력값입니다.")
  private String title;

  private String description;
  private Long customerUid;
}