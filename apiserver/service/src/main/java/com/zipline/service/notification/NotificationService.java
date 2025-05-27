package com.zipline.service.notification;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
  SseEmitter subscribe(String userId);

  void notify(String message);
}