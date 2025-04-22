package com.zipline.controller.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import com.zipline.service.message.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping("/")
  public Mono<ResponseEntity<String>> sendMessage(
      @RequestBody SendMessageRequestDTO[] requestBody) {

    return messageService.sendMessage(requestBody)
        .map(ResponseEntity::ok)
        .onErrorResume(e -> {
          System.err.println("Error occurred while sending message: " + e.getMessage());

          // 에러 발생 시 처리
          return Mono.just(
              ResponseEntity.status(500).body(e.toString()));
        });
  }
}