package com.zipline.service.message;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.entity.enums.MessageTemplateVariables;
import com.zipline.entity.message.MessageTemplate;
import com.zipline.entity.user.User;
import com.zipline.global.exception.message.MessageException;
import com.zipline.global.exception.message.errorcode.MessageErrorCode;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.message.MessageTemplateRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.message.dto.request.BirthdayNotificationMessageDTO;
import com.zipline.service.message.dto.request.ExpiredNotificationMessageDTO;
import com.zipline.service.message.dto.request.NotificationMessage;
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
  private final CustomerRepository customerRepository;


  @Value("${sms.fromNumber}")
  private String fromNumber;

  @Transactional
//  @Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
  public void sendNotificationMessage() {
    LocalDate today = LocalDate.now();
    List<User> users = userRepository.findByDeletedAtIsNull();

    for (User user : users) {
        try {
          LocalDate userEndDate = today.plusMonths(user.getNoticeMonth());
          processUserNotificationMessages(user, userEndDate);
          processBirthdayMessages(user, today);
        } catch (Exception e) {
            log.error("유저 {} 처리 중 오류 발생: {}", user.getUid(), e.getMessage(), e);
        }
    }
}

  private void processUserNotificationMessages(User user, LocalDate endDate) {
    try {
      List<Contract> expiringContracts = findExpiringContracts(user, endDate);
      MessageTemplate template = getMessageTemplate(user, MessageTemplateCategory.EXPIRED_NOTI);

      if (!expiringContracts.isEmpty()) {
        List<ExpiredNotificationMessageDTO> notifications = expiringContracts.stream()
            .map(contract -> createNotificationMessage(contract, template, user))
            .toList();

        processNotificationMessages(user, MessageTemplateCategory.EXPIRED_NOTI, notifications);
      }
    } catch (Exception e) {
      log.error("유저 {} 계약 만료 알림 처리 실패: {}", user.getUid(), e.getMessage(), e);
    }
  }

  private void processBirthdayMessages(User user, LocalDate today) {
    try {
      List<Customer> birthdayCustomers = findCustomersWithBirthdayToday(user, today);
      MessageTemplate template = getMessageTemplate(user, MessageTemplateCategory.BIRTHDAY);

      if (!birthdayCustomers.isEmpty()) {
        List<BirthdayNotificationMessageDTO> notifications = birthdayCustomers.stream()
            .map(customer -> BirthdayNotificationMessageDTO.builder()
                .customers(List.of(customer))
                .messageTemplate(template)
                .alertDateTime(LocalDateTime.of(LocalDate.now(), user.getNoticeTime()))
                .build())
            .toList();

        processNotificationMessages(user, MessageTemplateCategory.BIRTHDAY, notifications);
      }
    } catch (Exception e) {
      log.error("유저 {} 생일 알림 처리 실패: {}", user.getUid(), e.getMessage(), e);
    }
  }


  private void processNotificationMessages(User user, MessageTemplateCategory category,
      List<? extends NotificationMessage> notifications) {
    try {
      if (!notifications.isEmpty()) {
        notifications.forEach(this::sendScheduledMessage);
      }
    } catch (MessageException e) {
      log.error("유저 {} {} 알림 처리 실패: {}",
          user.getUid(),
          category.name(),
          e.getMessage(),
          e);
    }
  }

  private List<Customer> findCustomersWithBirthdayToday(User user, LocalDate today) {
    String todayMMdd = String.format("%02d%02d", today.getMonthValue(), today.getDayOfMonth());
    return customerRepository.findCustomersWithBirthdayToday(user.getUid(), todayMMdd);
  }

  private List<Contract> findExpiringContracts(User user, LocalDate endDate) {
    return contractRepository.findExpiringContractsByUser(
        user.getUid(),
        endDate
    );
  }

  private ExpiredNotificationMessageDTO createNotificationMessage(Contract contract, MessageTemplate template, User user) {
    return ExpiredNotificationMessageDTO.builder()
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

  private MessageTemplate getMessageTemplate(User user, MessageTemplateCategory category) {
    return messageTemplateRepository
        .findByCategoryAndUserUidAndDeletedAtIsNull(category, user.getUid())
        .orElseThrow(() -> new MessageException(
            category == MessageTemplateCategory.BIRTHDAY
                ? MessageErrorCode.BIRTHDAY_MESSAGE_TEMPLATE_NOT_FOUND
                : MessageErrorCode.EXPIRED_NOTI_MESSAGE_TEMPLATE_NOT_FOUND
        ));

  }

  private void sendScheduledMessage(NotificationMessage notificationData) {
    List<Map<String, Object>> messages = createMessagesList(notificationData);

    if (messages.isEmpty()) {
      return;
    }

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
        .onErrorResume(e -> {
          log.error("메시지 발송 중 오류 발생: {}", e.getMessage());
          return Mono.empty();
        })
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

  private List<Map<String, Object>> createMessagesList(NotificationMessage notificationData) {
    return notificationData.getTargetCustomers().stream()
        .filter(customer -> customer.getPhoneNo() != null)
        .map(customer -> createMessageMap(customer, notificationData.getMessageTemplate()))
        .toList();
  }

  private Map<String, Object> createMessageMap(Customer customer, MessageTemplate messageTemplate) {
    String formattedPhoneNo = customer.getPhoneNo().replaceAll("-", "");

    String messageContent = replaceTemplateVariables(messageTemplate.getContent(), Map.of(
        MessageTemplateVariables.NAME, customer.getName(),
        MessageTemplateVariables.BIRTH_DATE, customer.getBirthday(),
        MessageTemplateVariables.INTEREST_AREA, customer.getLegalDistrictCode()
    ));

    return Map.of(
        "to",formattedPhoneNo,
        "from", fromNumber,
        "text", messageContent
    );
  }

  private String replaceTemplateVariables(String template, Map<MessageTemplateVariables, String> variables) {
    return variables.entrySet().stream()
        .reduce(template,
            (acc, entry) -> acc.replace(entry.getKey().getTemplateKey(), entry.getValue()),
            (s1, s2) -> s1);
  }
}