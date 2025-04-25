package com.zipline.controller.message;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.request.SendMessageRequestDTO;
import com.zipline.service.message.MessageService;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "문자", description = "문자 / 문자 템플릿 관련 API")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping("")
  public ResponseEntity<String> sendMessage(@RequestBody List<SendMessageRequestDTO> request,
      Principal principal) {
    String result = messageService.sendMessage(request, Long.parseLong(principal.getName()));
    return ResponseEntity.ok(result);
  }

  @GetMapping("")
  public ResponseEntity<Object> getMessageHistory(@RequestParam(required = false) String criteria,
      @RequestParam(required = false) String cond, @RequestParam(required = false) String value, Principal principal) {

    MessageHistoryRequestDTO requestDTO = MessageHistoryRequestDTO.builder()
        .criteria(criteria)
        .cond(cond)
        .value(value)
        .build();

    ApiResponse<MessageHistoryResponseDTO> result = ApiResponse.ok("문자 발송 내역 조회에 성공했습니다.",
        messageService.getMessageHistory(requestDTO, Long.parseLong(principal.getName())));
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }


}