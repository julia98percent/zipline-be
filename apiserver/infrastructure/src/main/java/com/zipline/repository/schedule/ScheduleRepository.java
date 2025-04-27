package com.zipline.repository.schedule;


import com.zipline.entity.schedule.Schedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  Optional<Schedule> findByUidAndUserUidAndDeletedAtIsNull(Long scheduleUid, Long userUid);
}