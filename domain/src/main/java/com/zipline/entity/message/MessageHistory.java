package com.zipline.entity.message;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message_histories")
@Entity
@Getter
public class MessageHistory extends BaseTimeEntity {

  @Id
  private String groupUid;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Builder
  public MessageHistory(String groupUid, User user, LocalDateTime createdAt,
      LocalDateTime updatedAt, LocalDateTime deletedAt) {
    this.groupUid = groupUid;
    this.user = user;
  }
}