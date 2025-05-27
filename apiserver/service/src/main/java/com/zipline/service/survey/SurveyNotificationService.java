package com.zipline.service.survey;

import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import java.util.List;

public interface SurveyNotificationService {

  public void sendNewSurveyNotification(String surveyUlid, SurveyResponse surveyResponse,
      List<SurveyAnswer> answers);
}