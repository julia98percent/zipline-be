package com.zipline.controller.counsel;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.counsel.CounselCreateRequestDTO;
import com.zipline.dto.counsel.CounselResponseDTO;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.counsel.CounselService;

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

	@GetMapping("/counsels/{counselUid}")
	public ResponseEntity<?> getCounsel(@PathVariable Long counselUid, Principal principal) {
		ApiResponse<CounselResponseDTO> response = counselService.getCounsel(counselUid,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
