package com.zipline.service.notification;

import com.zipline.entity.notification.Notification;
import com.zipline.global.exception.notification.NotificationException;
import com.zipline.global.exception.notification.errorcode.NotificationErrorCode;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.notification.NotificationRepository;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  LocalDateTime TWO_WEEKS_AGO = LocalDateTime.now().minusWeeks(2);

  @Transactional(readOnly = true)
  public List<NotificationResponseDTO> getNotificationList(PageRequestDTO pageRequestDTO,
      Long userUid) {
    List<Notification> notificationList = notificationRepository.findRecentNotifications(
        userUid, TWO_WEEKS_AGO, pageRequestDTO.toPageable());

    return notificationList.stream()
        .map(NotificationResponseDTO::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public NotificationResponseDTO modifyNotificationToRead(Long notificationUid,
      Long userUid) {
    Notification savedNotification = notificationRepository.findByUidAndUserUid(
        notificationUid, userUid).orElseThrow(() -> new NotificationException(
        NotificationErrorCode.NOTIFICATION_NOT_FOUND));

    savedNotification.markAsRead();
    notificationRepository.save(savedNotification);

    return NotificationResponseDTO.from(savedNotification);
  }
}