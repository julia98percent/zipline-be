package com.zipline.controller.notification;


import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.notification.EmitterService;
import com.zipline.service.notification.NotificationService;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    return emitterService.subscribe(String.valueOf(userDetails.getUserUid()));
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<NotificationResponseDTO> notificationList = notificationService.getNotificationList(
        pageRequestDTO,
        userDetails.getUserUid());
    return ResponseEntity.ok(ApiResponse.ok("알림 조회 완료", notificationList));
  }

  @PutMapping("/{notificationUid}/read")
  public ResponseEntity<ApiResponse<NotificationResponseDTO>> readNotification(
      @PathVariable Long notificationUid, @AuthenticationPrincipal CustomUserDetails userDetails) {
    NotificationResponseDTO notification = notificationService.modifyNotificationToRead(
        notificationUid,
        userDetails.getUserUid());
    return ResponseEntity.ok(ApiResponse.ok("알림 읽음 처리 완료", notification));
  }

  @PutMapping("/read")
  public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> readAllNotifications(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<NotificationResponseDTO> notification = notificationService.modifyAllNotificationsToRead(
        userDetails.getUserUid());
    return ResponseEntity.ok(ApiResponse.ok("모든 알림 읽음 처리 완료", notification));
  }

  @DeleteMapping("/{notificationUid}")
  public ResponseEntity<ApiResponse<Void>> deleteNotification(
      @PathVariable Long notificationUid, @AuthenticationPrincipal CustomUserDetails userDetails) {
    notificationService.deleteNotification(
        notificationUid,
        userDetails.getUserUid());
    return ResponseEntity.ok(ApiResponse.ok("알림 삭제 완료"));
  }
}