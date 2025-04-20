package com.zipline.dto.survey;

import java.util.List;
import java.util.stream.Collectors;

import com.zipline.entity.survey.Choice;
import com.zipline.entity.survey.Question;
import lombok.Getter;


@Getter
public class QuestionResponseDTO {
	private Long id;
	private String title;
	private String description;
	private String type;
	private boolean isRequired;
	private List<ChoiceResponseDTO> choices;

	private QuestionResponseDTO(Question question, List<ChoiceResponseDTO> choices) {
		this.title = question.getTitle();
		this.id = question.getUid();
		this.description = question.getDescription();
		this.type = question.getQuestionType().name();
		this.isRequired = question.isRequired();
		this.choices = choices;
	}

	public static QuestionResponseDTO from(Question question, List<Choice> choices) {
		List<ChoiceResponseDTO> choiceResponseDTOList = choices.stream()
				.map(ChoiceResponseDTO::new)
				.collect(Collectors.toList());

		return new QuestionResponseDTO(question, choiceResponseDTOList);
	}
}