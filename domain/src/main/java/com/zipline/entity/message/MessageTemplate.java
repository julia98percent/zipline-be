package com.zipline.entity.message;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.enums.MessageTemplateCategory;
import com.zipline.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message_templates")
@Entity
@Getter
public class MessageTemplate extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uid;

  private String name;
  private MessageTemplateCategory category;
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Builder
  public MessageTemplate(String name , MessageTemplateCategory category, String content,
    User user) {
    this.name = name;
    this.category = category;
    this.content = content;
    this.user = user;
  }

  public void updateInfo(String name, MessageTemplateCategory category, String content) {
    this.name = name;
    this.category = category;
    this.content = content;
  }
}