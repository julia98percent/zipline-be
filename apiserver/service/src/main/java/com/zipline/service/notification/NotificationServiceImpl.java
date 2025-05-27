package com.zipline.service.notification;

import com.zipline.repository.notification.NotificationRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  private final NotificationRepository notificationRepository;

  public SseEmitter subscribe(String userId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    notificationRepository.save(userId, emitter);

    emitter.onCompletion(() -> notificationRepository.deleteById(userId));
    emitter.onTimeout(() -> notificationRepository.deleteById(userId));

    try {
      emitter.send(SseEmitter.event()
          .id(userId)
          .name("connect")
          .data("연결되었습니다."));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return emitter;
  }

  public void notify(String message) {
    notificationRepository.findAll().forEach((key, emitter) -> {
      try {
        emitter.send(SseEmitter.event()
            .name("notification")
            .data(message));

      } catch (IOException e) {
        notificationRepository.deleteById(key);
      }
    });
  }
}