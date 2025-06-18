package com.zipline.service.message;

import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import com.zipline.service.message.dto.response.MessageTemplateResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface MessageTemplateService {

  @Timed
  void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid);

  @Timed
  List<MessageTemplateResponseDTO> getMessageTemplateList(Long userUid);

  @Timed
  MessageTemplateResponseDTO modifyMessageTemplate(Long templateUid,
      MessageTemplateRequestDTO request, Long userUid);

  @Timed
  void deleteMessageTemplate(Long templateUid, Long userUid);
}