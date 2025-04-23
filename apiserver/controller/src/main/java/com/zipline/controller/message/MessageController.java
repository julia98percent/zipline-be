package com.zipline.controller.message;

import com.zipline.service.message.dto.message.request.SendMessageRequestDTO;
import com.zipline.service.message.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "문자", description = "문자 / 문자 템플릿 관련 API")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping("")
  public ResponseEntity<String> sendMessage(@RequestBody List<SendMessageRequestDTO> request) {
      String result = messageService.sendMessage(request);
      return ResponseEntity.ok(result);
  }
}