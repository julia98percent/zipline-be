package com.zipline.message;

import com.zipline.global.request.SendMessageRequestDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MessageClient {

  String sendMessages(List<SendMessageRequestDTO> messages);

  Map<String, Object> getMessageHistory(Map<String, String> queryParams);

  String createMessageGroup(Map<String, Object> messages);

  String updateMessageGroup(String groupId, Map<String, Object> messages);

  String scheduleMessages(String groupId, LocalDateTime scheduleDateTime);

}