package com.zipline.repository.message;

import com.zipline.entity.message.MessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {

}