package com.zipline.controller.counsel;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.counsel.CounselService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CounselController {

	private final CounselService counselService;

	@GetMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<CounselResponseDTO>> getCounsel(@PathVariable Long counselUid,
		Principal principal) {
		ApiResponse<CounselResponseDTO> response = counselService.getCounsel(counselUid,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> modifyCounsel(@PathVariable Long counselUid,
		@Valid @RequestBody CounselModifyRequestDTO requestDTO, Principal principal) {
		ApiResponse<Map<String, Long>> response = counselService.modifyCounsel(counselUid, requestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<Void>> deleteCounsel(@PathVariable Long counselUid, Principal principal) {
		ApiResponse<Void> response = counselService.deleteCounsel(counselUid, Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
