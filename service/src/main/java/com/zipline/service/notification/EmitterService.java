package com.zipline.service.notification;

import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {

  @Timed
  SseEmitter subscribe(String userId);

  @Timed
  void notify(NotificationResponseDTO response);
}