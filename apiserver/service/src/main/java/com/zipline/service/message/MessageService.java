package com.zipline.service.message;

import com.zipline.global.request.SendMessageRequestDTO;
import com.zipline.global.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import java.util.List;

public interface MessageService {

  void saveMessageHistory(String messageGroupId, Long userUID);

  String sendMessage(List<SendMessageRequestDTO> request, Long userUID);

  MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO request, Long userUID);
}