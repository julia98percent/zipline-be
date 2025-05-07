package com.zipline.service.message;

import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import com.zipline.service.message.dto.response.MessageTemplateResponseDTO;
import java.util.List;

public interface MessageTemplateService {
  void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);
  List<MessageTemplateResponseDTO> getMessageTemplateList(Long userUid);
  MessageTemplateResponseDTO modifyMessageTemplate(Long templateUid, MessageTemplateRequestDTO request, Long userUid);
  void deleteMessageTemplate(Long templateUid, Long userUid);
}