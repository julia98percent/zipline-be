package com.zipline.service.message;

import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.request.SendMessageRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import java.util.List;

public interface MessageService {
  String sendMessage(List<SendMessageRequestDTO> request);
  MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO request);
}