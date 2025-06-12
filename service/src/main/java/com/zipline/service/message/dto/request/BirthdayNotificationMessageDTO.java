package com.zipline.service.message.dto.request;

import com.zipline.entity.customer.Customer;
import com.zipline.entity.message.MessageTemplate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BirthdayNotificationMessageDTO implements NotificationMessage {
  private final List<Customer> customers;
  private final MessageTemplate messageTemplate;
  private final LocalDateTime alertDateTime;

  @Override
  public List<Customer> getTargetCustomers() {
    return customers;
  }
}