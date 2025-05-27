package com.zipline.entity.notification;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.enums.NotificationCategory;
import com.zipline.entity.message.MessageHistory;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "uid", nullable = false)
  private Long uid;

  @Column(name = "notification_category", nullable = false)
  private NotificationCategory category;

  private String content;

  @Column(name = "is_read", nullable = false)
  private boolean read;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_uid")
  private User user;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_history_uid")
  private MessageHistory messageHistory;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_response_uid")
  private SurveyResponse surveyResponse;

  @Builder
  public Notification(NotificationCategory category, String content, User user,
      MessageHistory messageHistory, SurveyResponse surveyResponse) {
    this.category = category;
    this.content = content;
    this.read = false;
    this.user = user;
    this.messageHistory = messageHistory;
    this.surveyResponse = surveyResponse;
  }
}