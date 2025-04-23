package com.zipline.service.message;

import com.zipline.dto.message.MessageTemplateRequestDTO;
import com.zipline.global.response.ApiResponse;

public interface MessageTemplateService {
  ApiResponse<MessageTemplateRequestDTO> createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);

}