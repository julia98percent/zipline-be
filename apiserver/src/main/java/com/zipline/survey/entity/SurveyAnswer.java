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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "survey_answers")
@Entity
public class SurveyAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_response_uid")
	private SurveyResponse surveyResponse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_uid")
	private Question question;

	private String answer;

	public SurveyAnswer(SurveyResponse surveyResponse, Question question, String answer) {
		this.surveyResponse = surveyResponse;
		this.question = question;
		this.answer = answer;
	}
}
