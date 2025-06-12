package com.zipline.service.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @JsonIgnore
  private boolean descriptionFieldPresent;
  @JsonIgnore
  private boolean customerUidFieldPresent;

  @JsonProperty("description")
  private void descriptionPresent(String description) {
    this.description = description;
    this.descriptionFieldPresent = true;
  }

  @JsonProperty("customerUid")
  private void customerUidPresent(Long customerUid) {
    this.customerUid = customerUid;
    this.customerUidFieldPresent = true;
  }

  public boolean hasDescription() {
    return descriptionFieldPresent;
  }

  public boolean hasCustomerUid() {
    return customerUidFieldPresent;
  }
}