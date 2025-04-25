package com.zipline.service.message;


import com.zipline.repository.message.MessageHistoryRepository;
import com.zipline.service.message.dto.request.MessageHistoryRequestDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
@RequiredArgsConstructor

public class MessageHistoryParamFormatter {
  private final MessageHistoryRepository messageHistoryRepository;

  public Map<String, String> formatQueryParams(MessageHistoryRequestDTO requestDTO, Long userUID) {
    Map<String, String> queryParams = new HashMap<>();

    List<String> userGroupIds = messageHistoryRepository.findGroupUidsByUserId(userUID);

    // 기존 검색 조건 처리
    StringBuilder criteriaBuilder = new StringBuilder();
    StringBuilder condBuilder = new StringBuilder();
    StringBuilder valueBuilder = new StringBuilder();

    // 기존 검색 조건이 있다면 추가
    if (StringUtils.hasText(requestDTO.getCriteria())) {
      criteriaBuilder.append(requestDTO.getCriteria());
      condBuilder.append(requestDTO.getCond());
      valueBuilder.append(requestDTO.getValue());
    }

// 각 groupId에 대한 검색 조건 추가
    for (String groupId : userGroupIds) {
      if (!criteriaBuilder.isEmpty()) {
        criteriaBuilder.append(",");
        condBuilder.append(",");
        valueBuilder.append(",");
      }
      criteriaBuilder.append("groupId");
      condBuilder.append("eq");
      valueBuilder.append(groupId);
    }

    if(requestDTO.getLimit() != null && requestDTO.getLimit() > 0) {
      queryParams.put("limit", String.valueOf(requestDTO.getLimit()));
    }

    if(requestDTO.getStartKey() != null && !requestDTO.getStartKey().isEmpty()) {
      queryParams.put("startKey", requestDTO.getStartKey());
    }

    queryParams.put("criteria", criteriaBuilder.toString());
    queryParams.put("cond", condBuilder.toString());
    queryParams.put("value", valueBuilder.toString());

    return queryParams;
  }
}