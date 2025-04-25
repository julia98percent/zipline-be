package com.zipline.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.message.MessageHistory;
import com.zipline.entity.user.User;
import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.message.MessageHistoryRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.request.SendMessageRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

  private final WebClient webClient;
  private final MessageHistoryParamFormatter messageHistoryParamFormatter;
  private final MessageHistoryRepository messageHistoryRepository;
  private final UserRepository userRepository;


  public void saveMessageHistory(String messageGroupId, Long userUID) {
    User loginedUser = userRepository.findById(userUID)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    MessageHistory messageHistory = MessageHistory.builder()
        .groupUid(messageGroupId)
        .user(loginedUser)
        .createdAt(LocalDateTime.now())
        .build();

    messageHistoryRepository.save(messageHistory);
  }

  @Transactional
  public String sendMessage(List<SendMessageRequestDTO> request, Long userUID) {

    try {
      Map<String, Object> wrappedRequest = Map.of("messages", request);

      String response = webClient.post()
          .uri("/send-many/detail")
          .bodyValue(wrappedRequest)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      ObjectMapper objectMapper = new ObjectMapper();
      String groupId = objectMapper.readTree(response).path("groupInfo").path("_id").asText();

      saveMessageHistory(groupId, userUID);
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
              status -> status.is4xxClientError() || status.is5xxServerError(),
              clientResponse -> clientResponse.bodyToMono(String.class)
                  .handle((errorBody, sink) -> {
                    log.error("Error response from external API: {}", errorBody);
                    sink.error(new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED));
                  })
          )
          .bodyToMono(MessageHistoryResponseDTO.class)
          .block();
    } catch (Exception e) {
      throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
    }
  }
}