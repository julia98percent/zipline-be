package com.zipline.message;

import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.global.request.SendMessageRequestDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolapiMessageClient implements MessageClient {

  private final WebClient webClient;

  @Override
  public String sendMessages(List<SendMessageRequestDTO> messages) {
    Map<String, Object> wrappedRequest = Map.of("messages", messages);

    return webClient.post()
        .uri("/send-many/detail")
        .bodyValue(wrappedRequest)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public Map<String, Object> getMessageHistory(Map<String, String> queryParams) {
    return webClient.get()
        .uri(uriBuilder -> {
          uriBuilder.path("/groups/");
          queryParams.forEach(uriBuilder::queryParam);
          return uriBuilder.build();
        })
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .handle((errorBody, sink) -> {
                  log.error("Error response from external API: {}", errorBody);
                  sink.error(
                      new MessageException(MessageErrorCode.MESSAGE_HISTORY_EXTERNAL_FAILED));
                })
        )
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
        })
        .block();
  }

  @Override
  public String createMessageGroup(Map<String, Object> messages) {
    return webClient.post()
        .uri("/groups/")
        .bodyValue(messages)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public String updateMessageGroup(String groupId, Map<String, Object> messages) {
    return webClient.put()
        .uri("/groups/{groupId}/messages", groupId)
        .bodyValue(messages)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public String scheduleMessages(String groupId, LocalDateTime scheduleDateTime) {
    return webClient.post()
        .uri("/groups/{groupId}/schedule", groupId)
        .bodyValue(Map.of("scheduledDate", scheduleDateTime.minusHours(9).toString()))
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
}