package com.zipline.service.schedule;

import com.zipline.service.schedule.dto.request.DateRangeRequest;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import com.zipline.service.schedule.dto.request.ScheduleModifyRequestDTO;
import com.zipline.service.schedule.dto.response.ScheduleResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface ScheduleService {

  @Timed
  void createSchedule(ScheduleCreateRequestDTO request, Long userUid);

  @Timed
  List<ScheduleResponseDTO> getScheduleList(DateRangeRequest request, Long userUid);

  @Timed
  ScheduleResponseDTO modifySchedule(Long userUid, Long scheduleUid,
      ScheduleModifyRequestDTO request);

  @Timed
  void deleteSchedule(Long scheduleUid, Long userUid);
}