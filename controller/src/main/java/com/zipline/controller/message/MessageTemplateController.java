package com.zipline.controller.message;


import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.message.MessageTemplateService;
import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import com.zipline.service.message.dto.response.MessageTemplateResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<ApiResponse<Void>> createMessageTemplate(
      @RequestBody MessageTemplateRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    messageTemplateService.createMessageTemplate(request, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.create("문자 템플릿 등록 성공");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<List<MessageTemplateResponseDTO>>> getMessageTemplateList(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<MessageTemplateResponseDTO> messageTemplateList = messageTemplateService.getMessageTemplateList(
        userDetails.getUserUid());
    ApiResponse<List<MessageTemplateResponseDTO>> response = ApiResponse.ok("문자 템플릿 목록 조회 성공",
        messageTemplateList);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/{templateUid}")
  public ResponseEntity<ApiResponse<MessageTemplateResponseDTO>> modifyMessageTemplate(
      @PathVariable Long templateUid, @Valid @RequestBody MessageTemplateRequestDTO request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    MessageTemplateResponseDTO modifiedMessageTemplate = messageTemplateService.modifyMessageTemplate(
        templateUid, request, userDetails.getUserUid());
    ApiResponse<MessageTemplateResponseDTO> response = ApiResponse.ok("문자 템플릿 수정 성공",
        modifiedMessageTemplate);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{templateUid}")
  public ResponseEntity<ApiResponse<Void>> deleteMessageTemplate(@PathVariable Long templateUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    messageTemplateService.deleteMessageTemplate(templateUid, userDetails.getUserUid());
    ApiResponse<Void> responseBody = ApiResponse.ok("문자 템플릿 삭제 성공");
    return ResponseEntity.ok(responseBody);
  }
}