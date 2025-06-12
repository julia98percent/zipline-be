package com.zipline.service.schedule;

import com.zipline.service.schedule.dto.request.DateRangeRequest;
import com.zipline.service.schedule.dto.request.ScheduleCreateRequestDTO;
import com.zipline.service.schedule.dto.request.ScheduleModifyRequestDTO;
import com.zipline.service.schedule.dto.response.ScheduleResponseDTO;
import java.util.List;

public interface ScheduleService {

  void createSchedule(ScheduleCreateRequestDTO request, Long userUid);
  List<ScheduleResponseDTO> getScheduleList(DateRangeRequest request, Long userUid);
  ScheduleResponseDTO modifySchedule(Long userUid, Long scheduleUid,
      ScheduleModifyRequestDTO request);
  void deleteSchedule(Long scheduleUid, Long userUid);
}