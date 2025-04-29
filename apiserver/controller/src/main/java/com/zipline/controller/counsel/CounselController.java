package com.zipline.controller.counsel;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.request.CounselFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.counsel.CounselService;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class CounselController {

	private final CounselService counselService;

	@GetMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<CounselResponseDTO>> getCounsel(@PathVariable Long counselUid,
		Principal principal) {
		CounselResponseDTO result = counselService.getCounsel(counselUid,
			Long.parseLong(principal.getName()));
		ApiResponse<CounselResponseDTO> response = ApiResponse.ok("상담 상세 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/counsels")
	public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getCounsels(
		@ModelAttribute PageRequestDTO pageRequestDTO,
		@ModelAttribute CounselFilterRequestDTO filterRequestDTO, Principal principal) {
		CounselPageResponseDTO result = counselService.getCounsels(pageRequestDTO, filterRequestDTO,
			Long.parseLong(principal.getName()));
		ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 목록 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> modifyCounsel(@PathVariable Long counselUid,
		@Valid @RequestBody CounselModifyRequestDTO requestDTO, Principal principal) {
		Map<String, Long> result = counselService.modifyCounsel(counselUid, requestDTO,
			Long.parseLong(principal.getName()));
		ApiResponse<Map<String, Long>> response = ApiResponse.ok("상담 수정에 성공하였습니다.", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/counsels/{counselUid}")
	public ResponseEntity<ApiResponse<Void>> deleteCounsel(@PathVariable Long counselUid, Principal principal) {
		counselService.deleteCounsel(counselUid, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.ok("상담 삭제에 성공하였습니다.");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
