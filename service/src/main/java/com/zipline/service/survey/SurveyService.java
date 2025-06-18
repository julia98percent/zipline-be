package com.zipline.service.survey;

import com.zipline.entity.user.User;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.survey.dto.request.SurveyCreateRequestDTO;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDetailDTO;
import com.zipline.service.survey.dto.response.SurveyResponseListDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface SurveyService {

  @Timed
  Map<String, String> createSurvey(SurveyCreateRequestDTO requestDTO, Long userUid);

  @Timed
  void createDefaultSurveyForUser(User user);

  @Timed
  SurveyResponseDTO getSurvey(String surveyUid);

  @Timed
  void submitSurvey(String surveyUid, List<SurveySubmitRequestDTO> requestDTOList,
      List<MultipartFile> files);

  @Timed
  SurveyResponseListDTO getSurveyResponses(PageRequestDTO pageRequestDTO, Long userUid);

  @Timed
  SurveyResponseDetailDTO getSubmittedSurvey(Long surveyResponseUid, Long userUid);
}