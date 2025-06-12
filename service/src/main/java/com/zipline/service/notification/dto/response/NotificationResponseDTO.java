package com.zipline.service.notification.dto.response;

import com.zipline.entity.enums.NotificationCategory;
import com.zipline.entity.notification.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
@Builder
public class NotificationResponseDTO {

  private Long uid;
  private NotificationCategory category;
  private String content;
  private boolean read;
  private String url;
  private LocalDateTime createdAt;


  public static NotificationResponseDTO from(Notification notification) {
    String urlVariable = String.valueOf(switch (notification.getCategory()) {
      case BIRTHDAY_MSG, CONTRACT_EXPIRED_MSG -> notification.getMessageHistory().getGroupUid();
      case NEW_SURVEY -> notification.getSurveyResponse().getUid();
      case SCHEDULE -> null;
    });

    return NotificationResponseDTO.builder()
        .uid(notification.getUid())
        .category(notification.getCategory())
        .read(notification.isRead())
        .createdAt(notification.getCreatedAt())
        .content(notification.getContent())
        .url(urlVariable)
        .build();
  }
}