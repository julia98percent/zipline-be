package com.zipline.service.notification;

import com.zipline.entity.notification.Notification;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.notification.NotificationRepository;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  LocalDateTime TWO_WEEKS_AGO = LocalDateTime.now().minusWeeks(2);

  public List<NotificationResponseDTO> getNotificationList(PageRequestDTO pageRequestDTO,
      Long userUid) {
    List<Notification> notificationList = notificationRepository.findRecentNotifications(
        userUid, TWO_WEEKS_AGO, pageRequestDTO.toPageable());

    return notificationList.stream()
        .map(NotificationResponseDTO::from)
        .collect(Collectors.toList());
  }
}