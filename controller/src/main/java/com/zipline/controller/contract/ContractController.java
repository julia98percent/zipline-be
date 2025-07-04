package com.zipline.controller.contract;


import com.zipline.global.request.ContractFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.contract.ContractHistoryService;
import com.zipline.service.contract.ContractService;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractHistoryResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.contract.dto.response.ContractResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "계약", description = "계약 관련 API")
@RequestMapping("/api/v1/contracts")
public class ContractController {

  private final ContractService contractService;
  private final ContractHistoryService contractHistoryService;

  @GetMapping("/{contractUid}")
  public ResponseEntity<ApiResponse<ContractResponseDTO>> getContract(
      @PathVariable Long contractUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ContractResponseDTO contractResponseDTO = contractService.getContract(contractUid,
        userDetails.getUserUid());
    ApiResponse<ContractResponseDTO> response = ApiResponse.ok("계약 상세 조회 성공", contractResponseDTO);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> registerContract(
      @Valid @RequestPart ContractRequestDTO contractRequestDTO,
      @RequestPart(required = false) List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    contractService.registerContract(contractRequestDTO, files, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.create("계약 등록 성공");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{contractUid}")
  public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable Long contractUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    contractService.deleteContract(contractUid, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.ok("계약 삭제 성공");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping(value = "/{contractUid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ContractResponseDTO>> modifyContract(
      @PathVariable Long contractUid,
      @Valid @RequestPart ContractRequestDTO contractRequestDTO,
      @RequestPart(required = false) List<MultipartFile> files,
      @RequestPart(value = "existingDocuments", required = false) List<ContractResponseDTO.DocumentDTO> existingDocs,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ContractResponseDTO contractResponseDTO = contractService.modifyContract(contractRequestDTO,
        contractUid, files,
        existingDocs, userDetails.getUserUid());

    ApiResponse<ContractResponseDTO> response = ApiResponse.ok("계약 수정 성공", contractResponseDTO);
    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<ContractListResponseDTO>> getContractList(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @ModelAttribute ContractFilterRequestDTO filter,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    ContractListResponseDTO response = contractService.getContractList(
        pageRequestDTO,
        userDetails.getUserUid(),
        filter
    );

    return ResponseEntity.ok(ApiResponse.ok("계약 목록 조회 성공", response));
  }

  @GetMapping("/{contractUid}/histories")
  public ResponseEntity<ApiResponse<List<ContractHistoryResponseDTO>>> getContractHistories(
      @PathVariable Long contractUid) {
    List<ContractHistoryResponseDTO> histories = contractHistoryService.getHistoriesByContractUid(
        contractUid);
    ApiResponse<List<ContractHistoryResponseDTO>> response = ApiResponse.ok("계약 히스토리 조회 성공",
        histories);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}