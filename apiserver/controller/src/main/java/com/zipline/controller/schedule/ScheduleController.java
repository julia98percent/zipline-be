package com.zipline.controller.schedule;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.schedule.ScheduleService;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/schedules")
@Tag(name = "일정", description = "일정 관련 api")
@RestController
public class ScheduleController {
  private final ScheduleService scheduleService;

  public ScheduleController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  @PostMapping("")
  public ResponseEntity<ApiResponse<Void>> createSchedule(@RequestBody @Valid ScheduleCreateRequestDTO request,
      Principal principal) {
    scheduleService.createSchedule(request, Long.parseLong(principal.getName()));

    ApiResponse<Void> responseBody = ApiResponse.ok("일정 생성 성공");
    return ResponseEntity.ok(responseBody);
  }

}