package com.zipline.service.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import reactor.core.publisher.Mono;

public interface MessageService {

  Mono<String> sendMessage(SendMessageRequestDTO[] request);
}