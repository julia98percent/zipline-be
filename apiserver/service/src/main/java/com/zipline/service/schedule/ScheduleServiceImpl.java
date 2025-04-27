package com.zipline.service.schedule;

import com.zipline.entity.customer.Customer;
import com.zipline.entity.schedule.Schedule;
import com.zipline.entity.user.User;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.schedule.ScheduleException;
import com.zipline.global.exception.schedule.errorcode.ScheduleErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.schedule.ScheduleRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void createSchedule(ScheduleCreateRequestDTO request, Long userUid) {
        validateScheduleTimeRequest(request);

        User user = userRepository.findById(userUid)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Customer customer = findCustomerIsExist(request.getCustomerId());


        Schedule schedule = Schedule.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .startDate(request.getStartDateTime())
            .endDate(request.getEndDateTime())
            .user(user)
            .customer(customer)
            .build();

        scheduleRepository.save(schedule);
    }

    private Customer findCustomerIsExist(Integer customerId) {
        if (customerId == null) {
            return null;
        }
        return customerRepository.findById(customerId.longValue())
            .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    private void validateScheduleTimeRequest(ScheduleCreateRequestDTO request) {
        if (request.getStartDateTime().isAfter(request.getEndDateTime())) {
            throw  new ScheduleException(ScheduleErrorCode.INVALID_SCHEDULE_TIME);
        }
    }
  @Transactional
  public void deleteSchedule(Long scheduleUid, Long userUid) {
    Schedule schedule = scheduleRepository.findByUidAndUserUidAndDeletedAtIsNull(
            scheduleUid, userUid)
        .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

    schedule.delete(LocalDateTime.now());
  }
}