package com.zipline.service.survey;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.zipline.dto.PageRequestDTO;
import com.zipline.dto.survey.SurveyCreateRequestDTO;
import com.zipline.dto.survey.SurveyResponseDTO;
import com.zipline.dto.survey.SurveyResponseDetailDTO;
import com.zipline.dto.survey.SurveyResponseListDTO;
import com.zipline.dto.survey.SurveySubmitRequestDTO;
import com.zipline.entity.user.User;
import com.zipline.global.response.ApiResponse;

public interface SurveyService {

	ApiResponse<Map<String, Long>> createSurvey(SurveyCreateRequestDTO requestDTO, Long agentUID);

	void createDefaultSurveyForUser(User user);

	ApiResponse<SurveyResponseDTO> getSurvey(Long surveyUID);

	ApiResponse<Void> submitSurvey(Long surveyUid, List<SurveySubmitRequestDTO> requestDTOList,
		List<MultipartFile> files);

	SurveyResponseListDTO getSurveyResponses(PageRequestDTO pageRequestDTO, Long userUid);

	ApiResponse<SurveyResponseDetailDTO> getSubmittedSurvey(Long surveyResponseUid, Long userUid);
}
