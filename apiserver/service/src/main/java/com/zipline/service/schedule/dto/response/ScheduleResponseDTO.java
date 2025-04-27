package com.zipline.service.schedule.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.schedule.Schedule;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class ScheduleResponseDTO {
  private Long uid;
  private String title;
  private String description;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime endDate;

  private Long customerUid;
  private String customerName;

  @Builder
  private ScheduleResponseDTO(Long uid, String title, String description,
      LocalDateTime startDate, LocalDateTime endDate,
      Customer customer) {
    this.uid = uid;
    this.title = title;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.customerUid = Optional.ofNullable(customer)
        .map(Customer::getUid)
        .orElse(null);
    this.customerName = Optional.ofNullable(customer)
        .map(Customer::getName)
        .orElse(null);

  }

  public static ScheduleResponseDTO from(Schedule schedule) {
    return new ScheduleResponseDTO(
        schedule.getUid(),
        schedule.getTitle(),
        schedule.getDescription(),
        schedule.getStartDate(),
        schedule.getEndDate(),
        schedule.getCustomer()
    );
  }
}