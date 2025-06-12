package com.zipline.repository.schedule;


import com.zipline.entity.schedule.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  Optional<Schedule> findByUidAndUserUidAndDeletedAtIsNull(Long scheduleUid, Long userUid);
  @Query("SELECT s FROM Schedule s " +
      "LEFT JOIN FETCH s.user u " +
      "WHERE s.user.uid = :userUid " +
      "AND s.deletedAt IS NULL " +
      "AND s.startDate <= :endDate " +
      "AND s.endDate >= :startDate")
  List<Schedule> findSchedulesInDateRange(
      @Param("userUid") Long userUid,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );
}