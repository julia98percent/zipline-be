package com.zipline.service.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCreateRequestDTO {

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @NotNull(message = "시작 시간은 필수 입력값입니다.")
  private LocalDateTime startDateTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @NotNull(message = "종료 시간은 필수 입력값입니다.")
  private LocalDateTime endDateTime;

  @Size(min = 2, max = 15, message = "일정 제목은 2자 이상 15자 이하로 입력해주세요.")
  @NotNull(message = "제목은 필수 입력값입니다.")
  private String title;

  @Size(min = 0, max = 200, message = "일정 설명은 200자 이하로 입력해주세요.")
  private String description;

  private Long customerUid;
}