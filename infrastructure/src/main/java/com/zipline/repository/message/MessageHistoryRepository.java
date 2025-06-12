package com.zipline.repository.message;

import com.zipline.entity.message.MessageHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {

  @Query("SELECT m.groupUid FROM MessageHistory m WHERE m.user.uid = :userUID")
  List<String> findGroupUidsByUserId(@Param("userUID") Long userUID);

  @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MessageHistory m WHERE m.user.uid = :userUID AND m.groupUid = :groupUID")
  boolean existsByUserUidAndGroupUid(@Param("userUID") Long userUID,
      @Param("groupUID") String groupUID);


}