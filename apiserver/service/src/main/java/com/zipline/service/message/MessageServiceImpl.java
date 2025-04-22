package com.zipline.service.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
          .retrieve().toString();

      log.info("메시지 전송 성공: {} 개의 메시지", request.size());
      return response;

    } catch (Exception e) {
      log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
      throw new RuntimeException("메시지 전송 실패: " + e.getMessage(), e);
    }
  }
}