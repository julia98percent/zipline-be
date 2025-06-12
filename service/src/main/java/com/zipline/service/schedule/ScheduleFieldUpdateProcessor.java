package com.zipline.service.schedule;

import com.zipline.entity.customer.Customer;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.repository.customer.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class ScheduleFieldUpdateProcessor {
  private final CustomerRepository customerRepository;

  public ScheduleFieldUpdateProcessor(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }


  public String processUpdate(String newValue, String currentValue, boolean isFieldOmitted) {
    // 필드가 생략된 경우
    if (isFieldOmitted) {
      return currentValue;
    }
    // 필드: null로 명시적으로 요청한 경우
    if (newValue == null) {
      return null;
    }
    return newValue;
  }

  public Customer processCustomerUpdate(Long customerUid, Customer currentCustomer, boolean isFieldOmitted) {
    // 필드가 생략된 경우
    if (isFieldOmitted) {
      return currentCustomer;
    }
    // 필드: null로 명시적으로 요청한 경우
    if (customerUid == null) {
      return null;
    }
    return customerRepository.findById(customerUid)
        .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
  }
}