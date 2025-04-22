package com.zipline.entity.survey;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.zipline.entity.enums.QuestionType;
import com.zipline.global.exception.custom.ChoiceNotAllowedException;

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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "questions")
@Entity
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@Column(name = "title", length = 20, nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type", nullable = false)
	private QuestionType questionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_uid")
	private Survey survey;

	@Column(name = "description", length = 200, nullable = false)
	private String description;

	@Column(name = "required", nullable = false)
	private boolean required;

	@OneToMany(mappedBy = "question", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Choice> choices = new ArrayList<>();

	public Question(String title, QuestionType questionType, String description, boolean required, Survey survey) {
		this.title = title;
		this.questionType = questionType;
		this.description = description;
		this.required = required;
		this.survey = survey;
	}

	public void addChoice(Choice choice) {
		if (this.questionType == QuestionType.FILE_UPLOAD
			|| this.questionType == QuestionType.SUBJECTIVE) {
			throw new ChoiceNotAllowedException("객관식이 아닌 문항에는 선택지를 추가할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		this.choices.add(choice);
	}
}