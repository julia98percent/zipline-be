package com.zipline.service.survey;

import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.Survey;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SurveyResponseService {

  public SurveyResponse createSurveyResponse(Survey survey,
      List<SurveySubmitRequestDTO> requestDTOList,
      List<MultipartFile> files, List<Question> questions);

}