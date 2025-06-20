package com.zipline.service.survey.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.Survey;

import lombok.Getter;

@Getter
public class SurveyResponseDTO {
	private Long id;
	private Long userId;
	private String title;
	private LocalDateTime createdAt;
	private List<QuestionResponseDTO> questions;

	private SurveyResponseDTO(Survey survey, List<QuestionResponseDTO> questions) {
		this.id = survey.getUid();
		this.userId = survey.getUser().getUid();
		this.title = survey.getTitle();
		this.createdAt = survey.getCreatedAt();
		this.questions = questions;
	}

	public static SurveyResponseDTO from(Survey survey, List<Question> questions) {
		List<QuestionResponseDTO> questionResponseDTOList = questions.stream()
			.map(question -> QuestionResponseDTO.from(question, question.getChoices()))
			.collect(Collectors.toList());
		return new SurveyResponseDTO(survey, questionResponseDTOList);
	}
}
