package com.zipline.service.message;

import com.zipline.service.message.dto.message.request.SendMessageRequestDTO;
import com.zipline.global.exception.custom.message.MessageCommonException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

  private final WebClient webClient;

  public String sendMessage(List<SendMessageRequestDTO> request) {

    try {
      Map<String, Object> wrappedRequest = Map.of("messages", request);

      String response = webClient.post()
          .uri("/send-many/detail")
          .bodyValue(wrappedRequest)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      log.info("메시지 전송 성공: {} 개의 메시지", request.size());
      return response;

    } catch (Exception e) {
      throw new MessageCommonException("메시지 전송 실패: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
    }
  }
}