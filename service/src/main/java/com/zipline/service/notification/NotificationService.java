package com.zipline.service.notification;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface NotificationService {

  @Timed
  List<NotificationResponseDTO> getNotificationList(PageRequestDTO pageRequestDTO,
      Long userUid);

  @Timed
  NotificationResponseDTO modifyNotificationToRead(Long notificationUid,
      Long userUid);

  @Timed
  List<NotificationResponseDTO> modifyAllNotificationsToRead(Long userUid);

  @Timed
  void deleteNotification(Long notificationUid,
      Long userUid);
}