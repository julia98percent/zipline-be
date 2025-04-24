package com.zipline.service.message;

import com.zipline.global.exception.custom.user.UserNotFoundException;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;
import com.zipline.service.message.dto.message.request.MessageTemplateRequestDTO;
import com.zipline.entity.message.MessageTemplate;
import com.zipline.entity.user.User;
import com.zipline.global.exception.custom.message.MessageTemplateDuplicatedException;
import com.zipline.global.response.ApiResponse;
import com.zipline.repository.message.MessageTemplateRepository;
import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTemplateServiceImpl implements MessageTemplateService {

  private final MessageTemplateRepository messageTemplateRepository;
  private final UserRepository userRepository;


  @Override
  @Transactional
  public void createMessageTemplate(MessageTemplateRequestDTO requestDTO, Long userUid) {
    if (requestDTO.getCategory().equals(MessageTemplateCategory.BIRTHDAY)
        || requestDTO.getCategory().equals(MessageTemplateCategory.EXPIRED_NOTI)) {
      messageTemplateRepository.findByCategoryAndUserUidAndDeletedAtIsNull(requestDTO.getCategory(),
          userUid).ifPresent(template -> {
        throw new MessageTemplateDuplicatedException(
            String.format("해당 카테고리(%s)의 메세지 템플릿이 이미 존재합니다.", requestDTO.getCategory()));
      });
    }

    User user = userRepository.findById(userUid)
        .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다: ", HttpStatus.NOT_FOUND));

    LocalDateTime now = LocalDateTime.now();

    MessageTemplate messageTemplate = MessageTemplate.builder()
        .name(requestDTO.getName())
        .category(requestDTO.getCategory())
        .content(requestDTO.getContent())
        .user(user)
        .createdAt(now)
        .updatedAt(now)
        .build();

    messageTemplateRepository.save(messageTemplate);
  }
}