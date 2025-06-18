package com.zipline.service.message;

import com.zipline.global.request.SendMessageRequestDTO;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.response.MessageListResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface MessageService {

  @Timed
  void saveMessageHistory(String messageGroupId, Long userUID);

  @Timed
  String sendMessage(List<SendMessageRequestDTO> request, Long userUID);

  @Timed
  MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO request, Long userUID);

  @Timed
  MessageListResponseDTO getMessageList(String messageGroupUid, Long userUID);
}