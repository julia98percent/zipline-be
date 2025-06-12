package com.zipline.service.survey;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.user.User;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.survey.dto.request.SurveyCreateRequestDTO;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDetailDTO;
import com.zipline.service.survey.dto.response.SurveyResponseListDTO;

public interface SurveyService {

	Map<String, String> createSurvey(SurveyCreateRequestDTO requestDTO, Long userUid);

	void createDefaultSurveyForUser(User user);

	SurveyResponseDTO getSurvey(String surveyUid);

	void submitSurvey(String surveyUid, List<SurveySubmitRequestDTO> requestDTOList, List<MultipartFile> files);

	SurveyResponseListDTO getSurveyResponses(PageRequestDTO pageRequestDTO, Long userUid);

	SurveyResponseDetailDTO getSubmittedSurvey(Long surveyResponseUid, Long userUid);
}
