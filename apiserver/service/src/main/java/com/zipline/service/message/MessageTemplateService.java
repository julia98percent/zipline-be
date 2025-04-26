package com.zipline.service.message;

import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import java.util.List;

public interface MessageTemplateService {
  void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);
  List<MessageTemplateResponseDTO> getMessageTemplateList(Long userUid);
}