package com.zipline.service.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import java.util.List;

public interface MessageService {
  String sendMessage(List<SendMessageRequestDTO> request);
}