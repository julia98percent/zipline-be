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
import com.zipline.service.schedule.dto.request.DateRangeRequest;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import com.zipline.service.schedule.dto.request.ScheduleModifyRequestDTO;
import com.zipline.service.schedule.dto.response.ScheduleResponseDTO;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ScheduleFieldUpdateProcessor scheduleFieldUpdateProcessor;

    @Override
    @Transactional
    public void createSchedule(ScheduleCreateRequestDTO request, Long userUid) {
        validateScheduleTimeRequest(request.getStartDateTime(), request.getEndDateTime());

        User user = userRepository.findById(userUid)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Customer customer = findCustomerIsExist(request.getCustomerUid());


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

    private Customer findCustomerIsExist(Long customerUid) {
        if (customerUid == null) {
            return null;
        }
        return customerRepository.findById(customerUid)
            .orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    private void validateScheduleTimeRequest(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_SCHEDULE_TIME);
        }
    }

    @Override
    @Transactional
    public ScheduleResponseDTO modifySchedule(Long userUid, Long scheduleUid,
        ScheduleModifyRequestDTO request) {
        validateScheduleTimeRequest(request.getStartDate(), request.getEndDate());

        Schedule schedule = scheduleRepository.findByUidAndUserUidAndDeletedAtIsNull(scheduleUid,
                userUid)
            .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        String newDescription = scheduleFieldUpdateProcessor.processUpdate(
            request.getDescription(),
            schedule.getDescription(),
            !request.hasDescription()
        );

        Customer newCustomer = scheduleFieldUpdateProcessor.processCustomerUpdate(
            request.getCustomerUid(),
            schedule.getCustomer(),
            !request.hasCustomerUid()
        );

        schedule.updateSchedule(
            request.getTitle(),
            newDescription,
            request.getStartDate(),
            request.getEndDate(),
            newCustomer
        );

        return ScheduleResponseDTO.from(schedule);
    }

    @Override
    public List<ScheduleResponseDTO> getScheduleList(DateRangeRequest request, Long userUid) {
        validateScheduleTimeRequest(request.getStartDate(), request.getEndDate());

        LocalDateTime startOfDay = request.getStartDate().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = request.getEndDate().toLocalDate().atTime(LocalTime.MAX);

        return scheduleRepository.findSchedulesInDateRange(userUid, startOfDay, endOfDay)
            .stream()
            .map(ScheduleResponseDTO::from)
            .toList();
    }
    @Override
    @Transactional
    public void deleteSchedule(Long scheduleUid, Long userUid) {
    Schedule schedule = scheduleRepository.findByUidAndUserUidAndDeletedAtIsNull(
            scheduleUid, userUid)
        .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

    schedule.delete(LocalDateTime.now());
  }
}