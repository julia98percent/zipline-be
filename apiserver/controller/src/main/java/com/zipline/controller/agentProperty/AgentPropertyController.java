package com.zipline.controller.agentProperty;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.request.AgentPropertyFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.agentProperty.AgentPropertyService;
import com.zipline.service.agentProperty.dto.request.AgentPropertyRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;
import com.zipline.service.contract.ContractService;
import com.zipline.service.contract.dto.response.ContractPropertyHistoryResponseDTO;
import com.zipline.service.contract.dto.response.ContractPropertyResponseDTO;
import com.zipline.service.counsel.CounselService;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "매물", description = "매물 관련 api")
@RequestMapping("/api/v1/properties")
public class AgentPropertyController {

	private final AgentPropertyService agentPropertyService;
	private final CounselService counselService;
	private final ContractService contractService;

	@GetMapping("/{propertyUid}")
	public ResponseEntity<ApiResponse<AgentPropertyResponseDTO>> getProperty(@PathVariable Long propertyUid,
		Principal principal) {
		AgentPropertyResponseDTO propertyResponseDTO = agentPropertyService.getProperty(propertyUid,
			Long.parseLong(principal.getName()));
		ApiResponse<AgentPropertyResponseDTO> response = ApiResponse.ok("매물 상세 조회 성공", propertyResponseDTO);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<Void>> registerProperty(
		@Valid @RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO, Principal principal) {
		agentPropertyService.registerProperty(agentPropertyRequestDTO, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.create("매물 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{propertyUid}")
	public ResponseEntity<ApiResponse<AgentPropertyResponseDTO>> modifyProperty(
		@Valid @RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO, @PathVariable Long propertyUid,
		Principal principal) {
		AgentPropertyResponseDTO propertyResponseDTO = agentPropertyService.modifyProperty(agentPropertyRequestDTO,
			propertyUid, Long.parseLong(principal.getName()));
		ApiResponse<AgentPropertyResponseDTO> response = ApiResponse.ok("매물 정보 수정 성공", propertyResponseDTO);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{propertyUid}")
	public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long propertyUid,
		Principal principal) {

		agentPropertyService.deleteProperty(propertyUid, Long.parseLong(principal.getName()));

		ApiResponse<Void> responseBody = ApiResponse.ok("매물 삭제 성공");
		return ResponseEntity.ok(responseBody);
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse<AgentPropertyListResponseDTO>> getPropertyList(
		@ModelAttribute PageRequestDTO pageRequestDTO,
		@Valid @ModelAttribute AgentPropertyFilterRequestDTO agentPropertyFilterRequestDTO,
		Principal principal) {
		AgentPropertyListResponseDTO propertyListResponseDTO = agentPropertyService.getAgentPropertyList(pageRequestDTO,
			Long.parseLong(principal.getName()), agentPropertyFilterRequestDTO);

		ApiResponse<AgentPropertyListResponseDTO> response = ApiResponse.ok("매물 목록 조회 성공", propertyListResponseDTO);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{propertyUid}/counsels")
	public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getCounselHistories(
		@ModelAttribute PageRequestDTO pageRequestDTO, @PathVariable Long propertyUid, Principal principal) {
		CounselPageResponseDTO result = counselService.getPropertyCounselHistories(pageRequestDTO,
			propertyUid, Long.parseLong(principal.getName()));
		ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 히스토리 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{propertyUid}/contract")
	public ResponseEntity<ApiResponse<ContractPropertyResponseDTO>> getPropertyContract(@PathVariable Long propertyUid,
		Principal principal) {
		ContractPropertyResponseDTO result = contractService.getPropertyContract(propertyUid,
			Long.parseLong(principal.getName()));
		ApiResponse<ContractPropertyResponseDTO> response = ApiResponse.ok("성사된 계약 정보 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{propertyUid}/contract-history")
	public ResponseEntity<ApiResponse<List<ContractPropertyHistoryResponseDTO>>> getPropertyContractHistory(
		@PathVariable Long propertyUid, Principal principal) {
		List<ContractPropertyHistoryResponseDTO> result = contractService.getPropertyContractHistories(
			propertyUid, Long.parseLong(principal.getName()));
		ApiResponse<List<ContractPropertyHistoryResponseDTO>> response = ApiResponse.ok("계약 히스토리 정보 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
