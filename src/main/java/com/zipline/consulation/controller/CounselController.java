package com.zipline.consulation.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.consulation.dto.CounselCreateRequestDTO;
import com.zipline.consulation.service.CounselService;
import com.zipline.global.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CounselController {

	private final CounselService counselService;

	@PostMapping("/customers/{customerUid}/counsels")
	public ResponseEntity<ApiResponse<Map<String, Long>>> createCounsel(@PathVariable Long customerUid,
		@Valid @RequestBody CounselCreateRequestDTO requestDTO, Principal principal) {
		ApiResponse<Map<String, Long>> response = counselService.createCounsel(customerUid, requestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
