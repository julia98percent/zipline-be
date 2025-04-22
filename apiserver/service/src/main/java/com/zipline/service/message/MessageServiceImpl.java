package com.zipline.service.message;

import com.zipline.dto.message.SendMessageRequestDTO;
import com.zipline.global.util.SmsSignatureGenerator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

  private final WebClient webClient;
  private final SmsSignatureGenerator signatureFactory;

  public String sendMessage(List<SendMessageRequestDTO> request) {


    Map<String, Object> wrappedRequest = Map.of("messages", request);

    return webClient.post()
        .uri("/send-many/detail")
        .bodyValue(wrappedRequest)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse -> clientResponse.bodyToMono(String.class).map(body -> {
              throw new RuntimeException(body);
            })
        ).bodyToMono(String.class);
  }
}