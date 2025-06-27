package com.zipline.controller.agentProperty;

import com.zipline.global.request.AgentPropertyFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.agentProperty.AgentPropertyService;
import com.zipline.service.agentProperty.dto.request.AgentPropertyRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;
import com.zipline.service.contract.ContractService;
import com.zipline.service.contract.dto.response.ContractPropertyHistoryResponseDTO;
import com.zipline.service.contract.dto.response.ContractPropertyResponseDTO;
import com.zipline.service.counsel.CounselService;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.excel.ExcelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "매물", description = "매물 관련 api")
@RequestMapping("/api/v1/properties")
public class AgentPropertyController {

  private final AgentPropertyService agentPropertyService;
  private final CounselService counselService;
  private final ExcelService excelService;
  private final ContractService contractService;

  @GetMapping("/{propertyUid}")
  public ResponseEntity<ApiResponse<AgentPropertyResponseDTO>> getProperty(
      @PathVariable Long propertyUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    AgentPropertyResponseDTO propertyResponseDTO = agentPropertyService.getProperty(propertyUid,
        userDetails.getUserUid());
    ApiResponse<AgentPropertyResponseDTO> response = ApiResponse.ok("매물 상세 조회 성공",
        propertyResponseDTO);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("")
  public ResponseEntity<ApiResponse<Void>> registerProperty(
      @Valid @RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    agentPropertyService.registerProperty(agentPropertyRequestDTO, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.create("매물 등록 성공");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{propertyUid}")
  public ResponseEntity<ApiResponse<AgentPropertyResponseDTO>> modifyProperty(
      @Valid @RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO,
      @PathVariable Long propertyUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    AgentPropertyResponseDTO propertyResponseDTO = agentPropertyService.modifyProperty(
        agentPropertyRequestDTO,
        propertyUid, userDetails.getUserUid());
    ApiResponse<AgentPropertyResponseDTO> response = ApiResponse.ok("매물 정보 수정 성공",
        propertyResponseDTO);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{propertyUid}")
  public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long propertyUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    agentPropertyService.deleteProperty(propertyUid, userDetails.getUserUid());

    ApiResponse<Void> responseBody = ApiResponse.ok("매물 삭제 성공");
    return ResponseEntity.ok(responseBody);
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<AgentPropertyListResponseDTO>> getPropertyList(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @Valid @ModelAttribute AgentPropertyFilterRequestDTO agentPropertyFilterRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    AgentPropertyListResponseDTO propertyListResponseDTO = agentPropertyService.getAgentPropertyList(
        pageRequestDTO,
        userDetails.getUserUid(), agentPropertyFilterRequestDTO);

    ApiResponse<AgentPropertyListResponseDTO> response = ApiResponse.ok("매물 목록 조회 성공",
        propertyListResponseDTO);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{propertyUid}/counsels")
  public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getCounselHistories(
      @ModelAttribute PageRequestDTO pageRequestDTO, @PathVariable Long propertyUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CounselPageResponseDTO result = counselService.getPropertyCounselHistories(pageRequestDTO,
        propertyUid, userDetails.getUserUid());
    ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 히스토리 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{propertyUid}/contract")
  public ResponseEntity<ApiResponse<ContractPropertyResponseDTO>> getPropertyContract(
      @PathVariable Long propertyUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ContractPropertyResponseDTO result = contractService.getPropertyContract(propertyUid,
        userDetails.getUserUid());
    ApiResponse<ContractPropertyResponseDTO> response = ApiResponse.ok("성사된 계약 정보 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{propertyUid}/contract-history")
  public ResponseEntity<ApiResponse<List<ContractPropertyHistoryResponseDTO>>> getPropertyContractHistory(
      @PathVariable Long propertyUid, @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<ContractPropertyHistoryResponseDTO> result = contractService.getPropertyContractHistories(
        propertyUid, userDetails.getUserUid());
    ApiResponse<List<ContractPropertyHistoryResponseDTO>> response = ApiResponse.ok(
        "계약 히스토리 정보 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/bulk")
  public ResponseEntity<ApiResponse<?>> registerPropertiesByExcel(
      @RequestPart(required = false) MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Map<String, Integer> result = excelService.registerPropertiesByExcel(file,
        userDetails.getUserUid());
    ApiResponse<Map<String, Integer>> response = ApiResponse.create("매물 엑셀 등록에 성공하였습니다.", result);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}