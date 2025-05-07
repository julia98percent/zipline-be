package com.zipline.service.message;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.entity.message.MessageTemplate;
import com.zipline.entity.user.User;
import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.message.MessageTemplateRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.message.dto.request.NotificationMessageDTO;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationMessageServiceImpl implements NotificationMessageService {

  private final WebClient webClient;
  private final MessageTemplateRepository messageTemplateRepository;
  private final ContractRepository contractRepository;
  private final UserRepository userRepository;

  @Value("${sms.fromNumber}")
  private String fromNumber;

  @Transactional
  @Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
  public void sendNotificationMessage() {
    LocalDate today = LocalDate.now();
    List<User> users = userRepository.findByDeletedAtIsNull();

    for (User user : users) {
        try {
            LocalDate userEndDate = today.plusMonths(user.getNoticeMonth());
            processUserNotificationMessages(user, userEndDate);
        } catch (Exception e) {
            log.error("유저 {} 처리 중 오류 발생: {}", user.getUid(), e.getMessage(), e);
        }
    }
}

private void processUserNotificationMessages(User user, LocalDate endDate) {
    try {
      MessageTemplate template = getMessageTemplate(user);
      List<Contract> expiringContracts = findExpiringContracts(user, endDate);
        if (!expiringContracts.isEmpty()) {
            scheduleUserNotifications(user, expiringContracts, template);
        }
    } catch (MessageException e) {
        log.error("유저 {} 알림 처리 실패: {}", user.getUid(), e.getMessage(), e);
    }
}

  private void scheduleUserNotifications(User user, List<Contract> contracts, MessageTemplate template) {
    contracts.stream()
        .map(contract -> createNotificationMessage(contract, template, user))
        .forEach(this::sendScheduledMessage);
  }

  private List<Contract> findExpiringContracts(User user, LocalDate endDate) {
    return contractRepository.findExpiringContractsByUser(
        user.getUid(),
        endDate
    );
  }

  private NotificationMessageDTO createNotificationMessage(Contract contract, MessageTemplate template, User user) {
    return NotificationMessageDTO.builder()
        .contract(contract)
        .messageTemplate(template)
        .alertDateTime(calculateAlertDateTime(contract, user))
        .build();
  }

  private LocalDateTime calculateAlertDateTime(Contract contract, User user) {
    LocalDate alertDate = contract.getContractEndDate().minusMonths(user.getNoticeMonth());
    LocalDate adjustedAlertDate = alertDate.withDayOfMonth(Math.min(alertDate.lengthOfMonth(), contract.getContractEndDate().getDayOfMonth()));
    return LocalDateTime.of(adjustedAlertDate, user.getNoticeTime());
  }

  private MessageTemplate getMessageTemplate(User user) {
    return messageTemplateRepository
        .findByCategoryAndUserUidAndDeletedAtIsNull(
            MessageTemplateCategory.EXPIRED_NOTI,
            user.getUid()
        )
        .orElseThrow(() -> new MessageException(MessageErrorCode.EXPIRED_NOTI_MESSAGE_TEMPLATE_NOT_FOUND));
  }

  private void sendScheduledMessage(NotificationMessageDTO notificationData) {
    List<Map<String, Object>> messages = createMessagesList(notificationData);
    Map<String, Object> wrappedRequest = Map.of("messages", messages);

    webClient.post()
        .uri("/groups/")
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(response -> {
          String groupId = response.replaceAll(".*\"groupId\"\\s*:\\s*\"([^\"]+)\".*", "$1");


          return sendMessagesToGroup(groupId, wrappedRequest, notificationData.getAlertDateTime());
        })
        .doOnSuccess(response -> log.info("메시지 예약 발송 설정 완료 - Response: {}", response))
        .doOnError(error -> log.error("메시지 발송 실패: {}", error.getMessage(), error))
        .subscribe();
}

private Mono<String> sendMessagesToGroup(String groupId, Map<String, Object> wrappedRequest,
    LocalDateTime alertDateTime) {
    return webClient.put()
        .uri("/groups/{groupId}/messages", groupId)
        .bodyValue(wrappedRequest)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> {
                    log.error("메시지 그룹 추가 실패 - Group ID: {}, Error: {}", groupId, errorMessage);
                    return Mono.error(new RuntimeException("메시지 그룹 추가 실패: " + errorMessage));
                })
        )
        .bodyToMono(String.class)
        .flatMap(response -> scheduleMessages(groupId, alertDateTime));
}

  private Mono<String> scheduleMessages(String groupId, LocalDateTime alertDateTime) {
    return webClient.post()
        .uri("/groups/{groupId}/schedule", groupId)
        .bodyValue(Map.of("scheduledDate", alertDateTime.minusHours(9).toString()))
        .retrieve()
        .bodyToMono(String.class);
  }

  private List<Map<String, Object>> createMessagesList(NotificationMessageDTO notificationData) {
    return notificationData.getContract().getCustomerContracts().stream()
        .map(CustomerContract::getCustomer)
        .filter(customer -> customer.getPhoneNo() != null)
        .map(customer -> createMessageMap(customer, notificationData.getMessageTemplate()))
        .toList();
  }

  private Map<String, Object> createMessageMap(Customer customer, MessageTemplate messageTemplate) {
    String formattedPhoneNo = customer.getPhoneNo().replaceAll("-", "");
    return Map.of(
        "to",formattedPhoneNo,
        "from", fromNumber,
        "text", messageTemplate.getContent()
    );
  }
}