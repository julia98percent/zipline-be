package com.zipline.repository.notification;

import com.zipline.entity.notification.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("SELECT n FROM Notification n WHERE n.user.uid = :userUid AND n.deletedAt IS NULL AND n.createdAt >= :twoWeeksAgo ORDER BY n.createdAt desc")
  List<Notification> findRecentNotifications(@Param("userUid") Long userUid,
      @Param("twoWeeksAgo") LocalDateTime twoWeeksAgo, Pageable pageable);

  Optional<Notification> findByUidAndUserUidAndDeletedAtNull(Long uid, Long userUid);

  @Modifying
  @Query("UPDATE Notification n SET n.read = true WHERE n.user.uid = :userUid AND n.deletedAt IS NULL")
  void markAllAsReadByUserUid(@Param("userUid") Long userUid);

  List<Notification> findAllByUserUidAndReadIsFalseAndDeletedAtNull(Long userUid);
}