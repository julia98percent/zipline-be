package com.zipline.service.survey;

import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.Survey;
import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.repository.survey.SurveyAnswerRepository;
import com.zipline.repository.survey.SurveyResponseRepository;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SurveyResponseServiceImpl implements SurveyResponseService {

  private final SurveyResponseRepository surveyResponseRepository;
  private final SurveyAnswerRepository surveyAnswerRepository;
  private final SurveyAnswerFactory surveyAnswerFactory;
  private final FileAnswerProcessor fileAnswerProcessor;

  @Transactional
  public SurveyResponse createSurveyResponse(Survey survey,
      List<SurveySubmitRequestDTO> requestDTOList,
      List<MultipartFile> files, List<Question> questions) {

    SurveyResponse surveyResponse = new SurveyResponse(survey, null, LocalDateTime.now());
    surveyResponseRepository.save(surveyResponse);

    List<SurveyAnswer> generalAnswers = createGeneralAnswers(requestDTOList, questions,
        surveyResponse);

    List<SurveyAnswer> fileAnswers = fileAnswerProcessor.processFileAnswers(files, questions,
        surveyResponse);

    List<SurveyAnswer> allAnswers = new ArrayList<>();
    allAnswers.addAll(generalAnswers);
    allAnswers.addAll(fileAnswers);
    surveyAnswerRepository.saveAll(allAnswers);

    return surveyResponse;
  }

  private List<SurveyAnswer> createGeneralAnswers(List<SurveySubmitRequestDTO> requestDTOList,
      List<Question> questions, SurveyResponse surveyResponse) {
    return requestDTOList.stream()
        .map(dto -> surveyAnswerFactory.createAnswer(dto, questions, surveyResponse))
        .collect(Collectors.toList());
  }
}