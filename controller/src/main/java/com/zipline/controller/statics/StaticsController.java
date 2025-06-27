package com.zipline.controller.statics;


import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.statics.StaticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/statics")
@Tag(name = "통계 API", description = "통계 관련 API")
@RestController
public class StaticsController {

  private final StaticsService staticsService;

  @GetMapping("/recent-contracts")
  @Operation(summary = "최근 계약 건수 조회", description = "최근 30일 이내 생성된 계약 건수를 조회합니다.")
  public ResponseEntity<ApiResponse<Integer>> getRecentContractCount(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    int count = staticsService.getRecentContractCount(userDetails.getUserUid());
    ApiResponse<Integer> response = ApiResponse.ok("Successfully retrieved recent contract count.",
        count);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/ongoing-contracts")
  @Operation(summary = "진행 중인 계약 건수 조회", description = "진행 중인 계약 건수를 조회합니다.")
  public ResponseEntity<ApiResponse<Integer>> getOngoingContractCount(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    int count = staticsService.getOngoingContractCount(userDetails.getUserUid());
    ApiResponse<Integer> response = ApiResponse.ok("Successfully retrieved ongoing contract count.",
        count);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/completed-contracts")
  @Operation(summary = "완료된 계약 건수 조회", description = "완료된 계약 건수를 조회합니다.")
  public ResponseEntity<ApiResponse<Integer>> getCompletedContractCount(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    int count = staticsService.getCompletedContractCount(userDetails.getUserUid());
    ApiResponse<Integer> response = ApiResponse.ok(
        "Successfully retrieved completed contract count.", count);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/recent-customers")
  @Operation(summary = "최근 고객 수 조회", description = "최근 30일 이내 확보한 고객 수를 조회합니다.")
  public ResponseEntity<ApiResponse<Integer>> getRecentCustomerCount(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    int count = staticsService.getRecentCustomerCount(userDetails.getUserUid());
    ApiResponse<Integer> response = ApiResponse.ok("Successfully retrieved recent customer count.",
        count);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}