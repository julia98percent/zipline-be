package com.zipline.controller.counsel;

import com.zipline.global.request.CounselFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.counsel.CounselService;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class CounselController {

  private final CounselService counselService;

  @GetMapping("/counsels/{counselUid}")
  public ResponseEntity<ApiResponse<CounselResponseDTO>> getCounsel(@PathVariable Long counselUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CounselResponseDTO result = counselService.getCounsel(counselUid,
        userDetails.getUserUid());
    ApiResponse<CounselResponseDTO> response = ApiResponse.ok("상담 상세 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/counsels")
  public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getCounsels(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @ModelAttribute CounselFilterRequestDTO filterRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CounselPageResponseDTO result = counselService.getCounsels(pageRequestDTO, filterRequestDTO,
        userDetails.getUserUid());
    ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 목록 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/dashboard/counsels")
  public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getDashboardCounsels(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @RequestParam(defaultValue = "DUE_DATE") String sortType,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CounselPageResponseDTO result = counselService.getDashBoardCounsels(pageRequestDTO, sortType,
        userDetails.getUserUid());
    ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 목록 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/counsels/{counselUid}")
  public ResponseEntity<ApiResponse<Map<String, Long>>> modifyCounsel(@PathVariable Long counselUid,
      @Valid @RequestBody CounselModifyRequestDTO requestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Map<String, Long> result = counselService.modifyCounsel(counselUid, requestDTO,
        userDetails.getUserUid());
    ApiResponse<Map<String, Long>> response = ApiResponse.ok("상담 수정에 성공하였습니다.", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/counsels/{counselUid}")
  public ResponseEntity<ApiResponse<Void>> deleteCounsel(@PathVariable Long counselUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    counselService.deleteCounsel(counselUid, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.ok("상담 삭제에 성공하였습니다.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}