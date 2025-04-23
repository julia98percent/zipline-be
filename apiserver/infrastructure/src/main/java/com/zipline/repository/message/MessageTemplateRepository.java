package com.zipline.repository.message;

import com.zipline.entity.message.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageTemplateRepository  extends JpaRepository<MessageTemplate, Long> {
}