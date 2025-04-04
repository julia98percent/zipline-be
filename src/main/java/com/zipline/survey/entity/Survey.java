package com.zipline.survey.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.User;
import com.zipline.survey.entity.enums.SurveyStatus;

import jakarta.persistence.CascadeType;
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
	private Long uid;

	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid")
	private User user;

	@OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Question> questions = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private SurveyStatus status;

	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public Survey(String title, User user, SurveyStatus status, LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.title = title;
		this.user = user;
		this.status = status;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}
}
