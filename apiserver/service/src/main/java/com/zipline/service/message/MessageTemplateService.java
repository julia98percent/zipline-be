package com.zipline.service.message;

import com.zipline.service.message.dto.message.request.MessageTemplateRequestDTO;

public interface MessageTemplateService {
  void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);

}