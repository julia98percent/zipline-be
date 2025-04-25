package com.zipline.service.message;

import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.request.SendMessageRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
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
  private final MessageHistoryParamFormatter messageHistoryParamFormatter;

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
      throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
    }
  }

  public MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO requestDTO) {
    try {
      Map<String, String> queryParams = messageHistoryParamFormatter.formatQueryParams(requestDTO);

      return webClient.get()
          .uri(uriBuilder -> {
            uriBuilder.path("/list-old/");
            queryParams.forEach(uriBuilder::queryParam);
            return uriBuilder.build();
          })
          .retrieve()
          .onStatus(
              // 외부 api 에러 메세지 리턴용
              status -> status.is4xxClientError() || status.is5xxServerError(),
              clientResponse -> clientResponse.bodyToMono(String.class)
                  .handle((errorBody, sink) -> {
                    log.error("Error response from external API: {}", errorBody);
                    sink.error(new MessageCommonException(
                        "External API error: " + errorBody,
                        (HttpStatus) clientResponse.statusCode()
                    ));
                  })
          )
          .bodyToMono(MessageHistoryResponseDTO.class)
          .block();
    } catch (Exception e) {
      throw new MessageCommonException("메세지 발송 내역 조회 실패: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }
}