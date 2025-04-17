package com.zipline.survey.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "choices")
@Entity
public class Choice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;
	private String text;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_uid")
	private Question question;

	public Choice(String text, Question question) {
		this.text = text;
		this.question = question;
	}
}
