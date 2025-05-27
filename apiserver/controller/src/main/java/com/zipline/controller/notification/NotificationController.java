package com.zipline.controller.notification;

import com.zipline.global.jwt.TokenProvider;
import com.zipline.service.notification.NotificationService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
  private final NotificationService notificationService;
  private  final TokenProvider tokenProvider;
  @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(
      Principal principal) {

    return notificationService.subscribe(principal.getName());
  }
}