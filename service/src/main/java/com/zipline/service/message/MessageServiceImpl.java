package com.zipline.service.message;

import static com.zipline.service.message.MessageHistoryMapper.mapToDTO;
import static com.zipline.service.message.MessageHistoryMapper.messageListMapToDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.message.MessageHistory;
import com.zipline.entity.user.User;
import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.SendMessageRequestDTO;
import com.zipline.message.MessageClient;
import com.zipline.repository.message.MessageHistoryRepository;
import com.zipline.repository.region.RegionRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import com.zipline.service.message.dto.response.MessageHistoryResponseDTO;
import com.zipline.service.message.dto.response.MessageListResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

  private final MessageClient messageClient;
  private final MessageHistoryParamFormatter messageHistoryParamFormatter;
  private final MessageHistoryRepository messageHistoryRepository;
  private final UserRepository userRepository;
  private final RegionRepository regionRepository;


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

  private String replaceTemplateValues(String text) {
    if (text == null) {
      return null;
    }

    Pattern pattern = Pattern.compile("\\$\\$###\\{([^}]+)}");
    Matcher matcher = pattern.matcher(text);

    StringBuilder result = new StringBuilder();
    while (matcher.find()) {
      String key = matcher.group(1);
      String replacement = getReplacementValue(key);
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(result);

    return result.toString();
  }

  private String getReplacementValue(String key) {
    if (key == null || !key.matches("\\d{10}")) {
      return key;
    }
    String cortarName = regionRepository.findCortarNameByCortarNo(key);

    return cortarName != null ? cortarName : key;
  }

  @Transactional
  public String sendMessage(List<SendMessageRequestDTO> request, Long userUID) {

    try {
      List<SendMessageRequestDTO> processedRequest = request.stream()
          .map(msg -> {
            String processedText = replaceTemplateValues(msg.getText());
            return msg.withText(processedText);
          })
          .toList();

      String response = messageClient.sendMessages(processedRequest);

      ObjectMapper objectMapper = new ObjectMapper();
      String groupId = objectMapper.readTree(response).path("groupInfo").path("_id").asText();

      saveMessageHistory(groupId, userUID);
      log.info("메시지 전송 성공: {} 개의 메시지", request.size());

      return response;
    } catch (Exception e) {
      log.info(e.getMessage());
      throw new MessageException(MessageErrorCode.MESSAGE_SEND_FAILED);
    }
  }

  public MessageHistoryResponseDTO getMessageHistory(MessageHistoryRequestDTO requestDTO,
      Long userUID) {
    try {
      List<String> userGroupIds = messageHistoryRepository.findGroupUidsByUserId(userUID);

      if (userGroupIds.isEmpty()) {
        return MessageHistoryResponseDTO.emptyResponse();
      }

      Map<String, String> queryParams = messageHistoryParamFormatter.formatQueryParams(requestDTO,
          userGroupIds);

      Map<String, Object> messageHistory = messageClient.getMessageHistory(queryParams);
      return mapToDTO(messageHistory);
    } catch (Exception e) {
      log.error("Error response from internal API: {}", e.getMessage());
      throw new MessageException(MessageErrorCode.MESSAGE_HISTORY_INTERNAL_FAILED);
    }
  }

  public MessageListResponseDTO getMessageList(String messageGroupUid, Long userUID) {
    boolean isGroupUidExists = messageHistoryRepository.existsByUserUidAndGroupUid(userUID,
        messageGroupUid);
    if (!isGroupUidExists) {
      throw new MessageException(MessageErrorCode.MESSAGE_HISTORY_INTERNAL_FAILED);
    }
    Map<String, Object> messageList = messageClient.getMessageList(messageGroupUid);
    return messageListMapToDTO(messageList);

  }
}