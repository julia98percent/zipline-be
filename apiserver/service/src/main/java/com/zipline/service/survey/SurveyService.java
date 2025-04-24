package com.zipline.service.survey;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.user.User;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.survey.dto.request.SurveyCreateRequestDTO;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDetailDTO;
import com.zipline.service.survey.dto.response.SurveyResponseListDTO;

public interface SurveyService {

	ApiResponse<Map<String, Long>> createSurvey(SurveyCreateRequestDTO requestDTO, Long agentUID);

	void createDefaultSurveyForUser(User user);

	ApiResponse<SurveyResponseDTO> getSurvey(Long surveyUID);

	ApiResponse<Void> submitSurvey(Long surveyUid, List<SurveySubmitRequestDTO> requestDTOList,
		List<MultipartFile> files);

	SurveyResponseListDTO getSurveyResponses(PageRequestDTO pageRequestDTO, Long userUid);

	ApiResponse<SurveyResponseDetailDTO> getSubmittedSurvey(Long surveyResponseUid, Long userUid);
}
