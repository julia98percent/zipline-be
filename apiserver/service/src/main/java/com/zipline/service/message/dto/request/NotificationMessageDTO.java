package com.zipline.service.message.dto.request;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.message.MessageTemplate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationMessageDTO {
  private final Contract contract;
  private final MessageTemplate messageTemplate;
  private final LocalDateTime alertDateTime;

}