package com.zipline.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.AgentPropertyRequestDTO;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.AgentPropertyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
public class PropertyController {

	private final AgentPropertyService agentPropertyService;

	@PostMapping("")
	public ResponseEntity<ApiResponse<Void>> registerProperty(
		@RequestBody AgentPropertyRequestDTO agentPropertyRequestDTO, Principal principal) {
		agentPropertyService.registerProperty(agentPropertyRequestDTO, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.create("회원가입 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
