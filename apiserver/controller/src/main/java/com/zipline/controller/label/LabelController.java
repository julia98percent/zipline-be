package com.zipline.controller.label;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.label.LabelService;
import com.zipline.service.label.dto.request.LabelRequestDTO;

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

}
