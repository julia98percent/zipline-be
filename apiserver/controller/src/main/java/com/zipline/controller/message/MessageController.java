package com.zipline.controller.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import com.zipline.service.message.MessageService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

    return messageService.sendMessage(requestBody)
        .map(ResponseEntity::ok)
        .onErrorResume(e -> {
          System.err.println("Error occurred while sending message: " + e.getMessage());

          // 에러 발생 시 처리
          return Mono.just(
              ResponseEntity.status(500).body(e.toString()));
        });
  @PostMapping("")
  public ResponseEntity<String> sendMessage(@RequestBody List<SendMessageRequestDTO> request) {
  }
}