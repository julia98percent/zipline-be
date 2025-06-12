package com.zipline.service.notification;

import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {

  SseEmitter subscribe(String userId);

  void notify(NotificationResponseDTO response);
}