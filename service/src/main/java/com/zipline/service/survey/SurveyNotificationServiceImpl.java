package com.zipline.service.survey;

import static com.zipline.entity.enums.NotificationCategory.NEW_SURVEY;

import com.zipline.entity.notification.Notification;
import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.entity.user.User;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.notification.NotificationRepository;
import com.zipline.repository.survey.SurveyResponseRepository;
import com.zipline.service.notification.EmitterService;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyNotificationServiceImpl implements SurveyNotificationService {

  private final NotificationRepository notificationRepository;
  private final EmitterService emitterService;
  private final SurveyResponseRepository surveyResponseRepository;

  @Transactional
  public void sendNewSurveyNotification(String surveyUlid, SurveyResponse surveyResponse,
      List<SurveyAnswer> answers) {
    User user = findUserBySurveyUlid(surveyUlid);
    String notificationMessage = createNotificationMessage(answers);

    Notification notification = new Notification(NEW_SURVEY, notificationMessage, user, null,
        surveyResponse);
    notificationRepository.save(notification);

    NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.from(notification);
    emitterService.notify(notificationResponseDTO);
  }

  private User findUserBySurveyUlid(String surveyUlid) {
    return surveyResponseRepository.findUserBySurveyUlid(surveyUlid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

  }


  private String createNotificationMessage(List<SurveyAnswer> answers) {
    String customerName = !answers.isEmpty() ? answers.get(0).getAnswer() : "-";
    String customerPhoneNo = answers.size() > 1 ? answers.get(1).getAnswer() : "-";

    return String.format("신규 고객이 상담을 요청했습니다. - 이름: %s / 전화번호: %s",
        customerName, customerPhoneNo);
  }
}