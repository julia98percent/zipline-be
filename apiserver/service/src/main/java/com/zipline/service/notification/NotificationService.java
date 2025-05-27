package com.zipline.service.notification;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {

  List<NotificationResponseDTO> getNotificationList(PageRequestDTO pageRequestDTO,
      Long userUid);

  // isRead
}