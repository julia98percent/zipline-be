package com.zipline.controller.notification;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.notification.EmitterService;
import com.zipline.service.notification.NotificationService;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final EmitterService emitterService;
  private final NotificationService notificationService;

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(
      Principal principal) {

    return emitterService.subscribe(principal.getName());
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(
      @ModelAttribute PageRequestDTO pageRequestDTO, Principal principal) {
    List<NotificationResponseDTO> notificationList = notificationService.getNotificationList(
        pageRequestDTO,
        Long.parseLong(principal.getName()));
    return ResponseEntity.ok(ApiResponse.ok("알림 조회 완료", notificationList));
  }
}