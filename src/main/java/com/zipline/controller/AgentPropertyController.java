package com.zipline.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.AgentPropertyRequestDTO;
import com.zipline.dto.AgentPropertyResponseDTO;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.AgentPropertyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
public class AgentPropertyController {

	private final AgentPropertyService agentPropertyService;

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
		@RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO, Principal principal) {
		agentPropertyService.registerProperty(agentPropertyRequestDTO, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.create("매물 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
