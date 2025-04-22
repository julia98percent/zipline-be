package com.zipline.entity.survey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.enums.SurveyStatus;
import com.zipline.entity.user.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "surveys")
@Entity
public class Survey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@Column(name = "title", length = 20, nullable = false)
	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid")
	private User user;

	@OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Question> questions = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private SurveyStatus status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public Survey(String title, User user, SurveyStatus status, LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.title = title;
		this.user = user;
		this.status = status;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}
}
