package com.zipline.survey.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.survey.dto.SurveyCreateRequestDTO;
import com.zipline.survey.service.SurveyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SurveyController {

	private final SurveyService surveyService;

	@PostMapping("/surveys")
	public ResponseEntity<ApiResponse<Void>> createSurvey(@RequestBody SurveyCreateRequestDTO requestDTO,
		Principal principal) {
		ApiResponse<Void> response = surveyService.createSurvey(requestDTO, Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
