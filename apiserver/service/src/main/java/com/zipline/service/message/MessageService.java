package com.zipline.service.message;

import com.zipline.global.request.SendMessageRequestDTO;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.response.MessageListResponseDTO;
import java.util.List;

public interface MessageService {

  void saveMessageHistory(String messageGroupId, Long userUID);

  String sendMessage(List<SendMessageRequestDTO> request, Long userUID);

  MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO request, Long userUID);

  MessageListResponseDTO getMessageList(String messageGroupUid, Long userUID);
}