package com.zipline.service.message.dto.request;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.message.MessageTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpiredNotificationMessageDTO implements NotificationMessage {
  private final Contract contract;
  private final MessageTemplate messageTemplate;
  private final LocalDateTime alertDateTime;

  @Override
  public List<Customer> getTargetCustomers() {
    return contract.getCustomerContracts().stream()
        .map(CustomerContract::getCustomer)
        .collect(Collectors.toList());
  }
}