package com.zipline.service.message;

import com.zipline.service.message.dto.message.request.SendMessageRequestDTO;
import java.util.List;

public interface MessageService {
  String sendMessage(List<SendMessageRequestDTO> request);
}