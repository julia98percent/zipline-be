package com.zipline.controller.label;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.label.LabelService;
import com.zipline.service.label.dto.request.LabelRequestDTO;
import com.zipline.service.label.dto.response.LabelListResponseDTO;
import com.zipline.service.label.dto.response.LabelResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/labels")
@RequiredArgsConstructor
public class LabelController {

	private final LabelService labelService;

	@PostMapping("")
	public ResponseEntity<ApiResponse<Void>> createLabel(
		@Valid @RequestBody LabelRequestDTO labelRequestDTO,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Long userUid = Long.parseLong(userDetails.getUsername());
		labelService.createLabel(userUid, labelRequestDTO);
		return ResponseEntity.ok(ApiResponse.create("라벨 생성 완료"));
	}

	@PatchMapping("/{labelUid}")
	public ResponseEntity<ApiResponse<LabelResponseDTO>> modifyLabel(
		@PathVariable Long labelUid,
		@Valid @RequestBody LabelRequestDTO labelRequestDTO,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Long userUid = Long.parseLong(userDetails.getUsername());
		LabelResponseDTO responseDTO = labelService.modifyLabel(userUid, labelUid, labelRequestDTO);
		return ResponseEntity.ok(ApiResponse.ok("라벨 수정 완료", responseDTO));
	}

	@DeleteMapping("/{labelUid}")
	public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Long labelUid,
		@AuthenticationPrincipal UserDetails userDetails) {
		Long userUid = Long.parseLong(userDetails.getUsername());
		labelService.deleteLabel(userUid, labelUid);
		ApiResponse<Void> responseBody = ApiResponse.ok("라벨 삭제 성공");
		return ResponseEntity.ok(responseBody);
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse<LabelListResponseDTO>> getLabelList(
		@AuthenticationPrincipal UserDetails userDetails) {
		Long userUid = Long.parseLong(userDetails.getUsername());
		LabelListResponseDTO listResponseDTO = labelService.getLabelList(userUid);
		return ResponseEntity.ok(ApiResponse.ok("라벨 조회 완료", listResponseDTO));
	}
}