package com.zipline.controller.survey;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.survey.SurveyService;
import com.zipline.service.survey.dto.request.SurveyCreateRequestDTO;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDetailDTO;
import com.zipline.service.survey.dto.response.SurveyResponseListDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "설문 API", description = "설문 관련 API")
@RestController
public class SurveyController {

	private final SurveyService surveyService;

	@Operation(summary = "설문 생성", description = "새로운 설문을 생성합니다.")
	@PostMapping("/surveys")
	public ResponseEntity<ApiResponse<Map<String, String>>> createSurvey(
		@Valid @RequestBody SurveyCreateRequestDTO requestDTO,
		Principal principal) {
		Map<String, String> result = surveyService.createSurvey(requestDTO,
			Long.parseLong(principal.getName()));
		ApiResponse<Map<String, String>> response = ApiResponse.create("설문 등록 완료", result);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "설문 조회", description = "공인중개사가 생성한 설문을 조회합니다.")
	@GetMapping("/surveys/{surveyUid}")
	public ResponseEntity<ApiResponse<SurveyResponseDTO>> getSurvey(@PathVariable Long surveyUid) {
		SurveyResponseDTO result = surveyService.getSurvey(surveyUid);
		ApiResponse<SurveyResponseDTO> response = ApiResponse.ok("설문 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "제출된 설문 결과 조회", description = "제출된 설문 결과를 조회합니다.")
	@GetMapping("/surveys/responses/{surveyResponseUid}")
	public ResponseEntity<ApiResponse<SurveyResponseDetailDTO>> getSubmittedSurvey(@PathVariable Long surveyResponseUid,
		Principal principal) {
		SurveyResponseDetailDTO result = surveyService.getSubmittedSurvey(surveyResponseUid,
			Long.parseLong(principal.getName()));
		ApiResponse<SurveyResponseDetailDTO> response = ApiResponse.ok("설문 상세조회에 성공하였습니다.", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "제출된 설문 리스트 조회", description = "제출된 설문 리스트를 조회합니다.")
	@GetMapping("/surveys/responses")
	public ResponseEntity<ApiResponse<SurveyResponseListDTO>> getSurveyResponses(PageRequestDTO pageRequestDTO,
		Principal principal) {
		SurveyResponseListDTO result = surveyService.getSurveyResponses(pageRequestDTO,
			Long.parseLong(principal.getName()));
		ApiResponse<SurveyResponseListDTO> response = ApiResponse.ok("제출된 설문 리스트 조회에 성공하였습니다.", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "설문 제출", description = "고객이 설문을 제출합니다.")
	@PostMapping(value = "/surveys/{surveyUid}/submit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
		MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ApiResponse<Void>> submitSurvey(@PathVariable Long surveyUid,
		@RequestPart(name = "requestDTO") List<SurveySubmitRequestDTO> requestDTO,
		@RequestPart(name = "files", required = false) List<MultipartFile> files) {
		surveyService.submitSurvey(surveyUid, requestDTO, files);
		ApiResponse<Void> response = ApiResponse.create("설문 제출에 성공하였습니다.");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
