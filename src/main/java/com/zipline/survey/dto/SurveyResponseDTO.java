package com.zipline.survey.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.zipline.survey.entity.Question;
import com.zipline.survey.entity.Survey;

import lombok.Getter;

@Getter
public class SurveyResponseDTO {
	private Long id;
	private Long userId;
	private String status;
	private LocalDateTime createdAt;
	private List<QuestionResponseDTO> questions;

	private SurveyResponseDTO(Survey survey, List<QuestionResponseDTO> questions) {
		this.id = survey.getUid();
		this.userId = survey.getUser().getUid();
		this.status = survey.getStatus().name();
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
