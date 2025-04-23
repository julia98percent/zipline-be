package com.zipline.controller.message;

import com.zipline.dto.message.MessageTemplateRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.message.MessageTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "문자", description = "메시지 / 메시지 템플릿 관련 API")
@RestController
@RequestMapping("/api/v1/templates")
public class MessageTemplateController {

  private final MessageTemplateService messageTemplateService;

  public MessageTemplateController(MessageTemplateService messageTemplateService) {
    this.messageTemplateService = messageTemplateService;
  }

  @PostMapping("")
  public ResponseEntity<MessageTemplateRequestDTO> createMessageTemplate(@RequestBody MessageTemplateRequestDTO request, 	Principal principal) {
      ApiResponse<MessageTemplateRequestDTO> messageTemplate = messageTemplateService.createMessageTemplate(request, Long.parseLong(principal.getName()));
      return ResponseEntity.ok(messageTemplate.getData());
  }
}