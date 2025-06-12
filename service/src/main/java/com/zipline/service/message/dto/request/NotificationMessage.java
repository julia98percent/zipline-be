package com.zipline.service.message.dto.request;

import com.zipline.entity.customer.Customer;
import com.zipline.entity.message.MessageTemplate;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationMessage {
  List<Customer> getTargetCustomers();
  MessageTemplate getMessageTemplate();
  LocalDateTime getAlertDateTime();
}