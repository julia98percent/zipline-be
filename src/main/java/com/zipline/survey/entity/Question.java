package com.zipline.survey.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.customer.ChoiceNotAllowedException;
import com.zipline.survey.entity.enums.QuestionType;

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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "questions")
@Entity
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@Enumerated(EnumType.STRING)
	private QuestionType questionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_uid")
	private Survey survey;

	private String text;

	@OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Choice> choices = new ArrayList<>();

	public Question(QuestionType questionType, String text, Survey survey) {
		this.questionType = questionType;
		this.text = text;
		this.survey = survey;
	}

	public void addChoice(Choice choice) {
		if (this.questionType == QuestionType.FILE_UPLOAD
			|| this.questionType == QuestionType.SUBJECTIVE) {
			throw new ChoiceNotAllowedException("객관식이 아닌 질문에는 선택지를 추가할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		this.choices.add(choice);
	}
}
