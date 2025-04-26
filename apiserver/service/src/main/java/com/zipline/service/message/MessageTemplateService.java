package com.zipline.service.message;

import com.zipline.service.message.dto.message.response.MessageTemplateResponseDTO;
import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import java.util.List;

public interface MessageTemplateService {
  void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);
  List<MessageTemplateResponseDTO> getMessageTemplateList(Long userUid);
  MessageTemplateResponseDTO modifyMessageTemplate(Long templateUid, MessageTemplateRequestDTO request, Long userUid);
  void deleteMessageTemplate(Long templateUid, Long userUid);
}