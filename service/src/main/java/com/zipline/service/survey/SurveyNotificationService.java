package com.zipline.service.survey;

import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface SurveyNotificationService {

  @Timed
  public void sendNewSurveyNotification(String surveyUlid, SurveyResponse surveyResponse,
      List<SurveyAnswer> answers);
}