package com.zipline.survey.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.survey.dto.SurveyCreateRequestDTO;
import com.zipline.survey.service.SurveyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "설문 API", description = "설문 관련 API")
@RestController
public class SurveyController {

	private final SurveyService surveyService;

	@PostMapping("/surveys")
	@Operation(summary = "설문 생성", description = "새로운 설문을 생성합니다.")
	public ResponseEntity<ApiResponse<Map<String, Long>>> createSurvey(
		@Valid @RequestBody SurveyCreateRequestDTO requestDTO,
		Principal principal) {
		ApiResponse<Map<String, Long>> response = surveyService.createSurvey(requestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
