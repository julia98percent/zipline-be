package com.zipline.service.message;

import com.zipline.service.message.dto.message.request.MessageTemplateRequestDTO;
import com.zipline.global.response.ApiResponse;

public interface MessageTemplateService {
  ApiResponse<MessageTemplateRequestDTO> createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);

}