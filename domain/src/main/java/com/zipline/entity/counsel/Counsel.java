package com.zipline.entity.counsel;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.CounselType;
import com.zipline.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "counsels")
@Entity
public class Counsel extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "uid", nullable = false)
  private Long uid;

  @Column(name = "title", length = 200, nullable = false)
  private String title;

  @Column(name = "counsel_date")
  private LocalDateTime counselDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_uid")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_uid")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agent_property_uid")
  private AgentProperty agentProperty;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private CounselType type;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "completed")
  private boolean completed;

  @Column(name = "content", length = 500, nullable = false)
  private String content;


  public Counsel(String title, LocalDateTime counselDate, CounselType type, LocalDate dueDate,
      User user,
      Customer customer, AgentProperty agentProperty, boolean completed, String content) {
    this.title = title;
    this.counselDate = counselDate;
    this.user = user;
    this.customer = customer;
    this.type = type;
    this.dueDate = dueDate;
    this.agentProperty = agentProperty;
    this.completed = completed;
    this.content = content;
  }


  public void update(String title, LocalDateTime counselDate, CounselType type, LocalDate dueDate,
      boolean completed, String content) {
    this.title = title;
    this.counselDate = counselDate;
    this.type = type;
    this.dueDate = dueDate;
    this.completed = completed;
    this.content = content;
  }
}