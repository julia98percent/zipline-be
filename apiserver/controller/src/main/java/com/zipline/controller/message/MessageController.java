package com.zipline.controller.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import com.zipline.service.message.MessageService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping("")
  public ResponseEntity<String> sendMessage(@RequestBody List<SendMessageRequestDTO> request) {
    try {
      String result = messageService.sendMessage(request);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      log.error("Error occurred while sending message: {}", e.getMessage());
      return ResponseEntity.status(500).body(e.toString());
    }
  }
}