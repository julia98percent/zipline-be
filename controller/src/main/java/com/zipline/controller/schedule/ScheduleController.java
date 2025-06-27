package com.zipline.controller.schedule;


import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.schedule.ScheduleService;
import com.zipline.service.schedule.dto.request.DateRangeRequest;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import com.zipline.service.schedule.dto.request.ScheduleModifyRequestDTO;
import com.zipline.service.schedule.dto.response.ScheduleResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<ApiResponse<Void>> createSchedule(
      @RequestBody @Valid ScheduleCreateRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    scheduleService.createSchedule(request, userDetails.getUserUid());

    ApiResponse<Void> responseBody = ApiResponse.ok("일정 생성 성공");
    return ResponseEntity.ok(responseBody);
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getScheduleList(
      @Valid @ModelAttribute DateRangeRequest dateRange,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    List<ScheduleResponseDTO> scheduleList = scheduleService.getScheduleList(dateRange,
        userDetails.getUserUid()
    );

    ApiResponse<List<ScheduleResponseDTO>> response = ApiResponse.ok("일정 목록 조회 성공", scheduleList);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{scheduleUid}")
  public ResponseEntity<ApiResponse<ScheduleResponseDTO>> modifySchedule(
      @PathVariable Long scheduleUid,
      @Valid @RequestBody ScheduleModifyRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ScheduleResponseDTO response = scheduleService.modifySchedule(
        userDetails.getUserUid(), scheduleUid, request);

    ApiResponse<ScheduleResponseDTO> responseBody = ApiResponse.ok("일정 수정 성공", response);
    return ResponseEntity.ok(responseBody);
  }

  @DeleteMapping("/{scheduleUid}")
  public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long scheduleUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    scheduleService.deleteSchedule(scheduleUid, userDetails.getUserUid());

    ApiResponse<Void> responseBody = ApiResponse.ok("일정 삭제 성공");
    return ResponseEntity.ok(responseBody);
  }

}