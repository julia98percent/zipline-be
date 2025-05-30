package com.zipline.controller.message;

import com.zipline.global.request.SendMessageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.message.MessageService;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.response.MessageListResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public ResponseEntity<ApiResponse<MessageHistoryResponseDTO>> getMessageHistory(
      @ModelAttribute MessageHistoryRequestDTO requestDTO,
      Principal principal) {

    ApiResponse<MessageHistoryResponseDTO> result = ApiResponse.ok("문자 발송 내역 조회에 성공했습니다.",
        messageService.getMessageHistory(requestDTO, Long.parseLong(principal.getName())));
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping("/{messageGroupUid}")
  public ResponseEntity<ApiResponse<MessageListResponseDTO>> getMessageList(
      @PathVariable String messageGroupUid,
      Principal principal) {

    ApiResponse<MessageListResponseDTO> result = ApiResponse.ok("문자 발송 상세 내역 조회에 성공했습니다.",
        messageService.getMessageList(messageGroupUid, Long.parseLong(principal.getName())));
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }


}