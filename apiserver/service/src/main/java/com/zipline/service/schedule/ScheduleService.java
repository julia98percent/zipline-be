package com.zipline.service.schedule;

import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;

public interface ScheduleService {

  void createSchedule(ScheduleCreateRequestDTO request, Long userUid);

  void deleteSchedule(Long scheduleUid, Long userUid);
}