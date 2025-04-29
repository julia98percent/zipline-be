package com.zipline.service.message;

import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.entity.message.MessageTemplate;
import com.zipline.entity.user.User;
import com.zipline.global.exception.message.MessageTemplateException;
import com.zipline.global.exception.message.errorcode.MessageTemplateErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.message.MessageTemplateRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.message.dto.message.response.MessageTemplateResponseDTO;
import com.zipline.service.message.dto.request.MessageTemplateRequestDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				throw new MessageTemplateException(MessageTemplateErrorCode.DUPLICATE_TEMPLATE_CATEGORY);
			});
		}
      User user = userRepository.findById(userUid)
          .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


      MessageTemplate messageTemplate = MessageTemplate.builder()
          .name(requestDTO.getName())
          .category(requestDTO.getCategory())
          .content(requestDTO.getContent())
          .user(user)
          .build();

      messageTemplateRepository.save(messageTemplate);
    }

	@Override
	@Transactional(readOnly = true)
	public List<MessageTemplateResponseDTO> getMessageTemplateList(Long userUid) {
		List<MessageTemplate> messageTemplateList = messageTemplateRepository.findByUserUidAndDeletedAtIsNull(userUid);
		return messageTemplateList.stream()
				.map(MessageTemplateResponseDTO::new)
				.toList();
	}

	@Override
	@Transactional
	public MessageTemplateResponseDTO modifyMessageTemplate(Long templateUid, MessageTemplateRequestDTO request, Long userUid) {
		MessageTemplate messageTemplate = messageTemplateRepository.findByUidAndUserUidAndDeletedAtIsNull(templateUid, userUid).orElseThrow(() -> new MessageTemplateException(
				MessageTemplateErrorCode.TEMPLATE_NOT_FOUND));

		if (!messageTemplate.getName().equals(request.getName())) {
			messageTemplateRepository.findByNameAndUserUidAndDeletedAtIsNull(request.getName(), userUid)
					.ifPresent(template -> {
						throw new MessageTemplateException(MessageTemplateErrorCode.DUPLICATE_TEMPLATE_NAME);
					});
		}

		messageTemplate.updateInfo(request.getName(), request.getContent());

		return new MessageTemplateResponseDTO(messageTemplate);
	}

  @Override
  @Transactional
  public void deleteMessageTemplate(Long templateUid, Long userUid) {
    MessageTemplate template = messageTemplateRepository.findByUidAndUserUidAndDeletedAtIsNull(templateUid, userUid)
        .orElseThrow(() -> new MessageTemplateException(MessageTemplateErrorCode.TEMPLATE_NOT_FOUND));


    template.delete(LocalDateTime.now());
  }
}