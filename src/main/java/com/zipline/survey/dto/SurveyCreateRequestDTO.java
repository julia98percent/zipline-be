package com.zipline.survey.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class SurveyCreateRequestDTO {

	private List<QuestionRequestDTO> questions = new ArrayList<>();

	@Getter
	public static class QuestionRequestDTO {
		private String text;
		private String type;
		private List<ChoiceRequestDTO> choices = new ArrayList<>();
	}

	@Getter
	public static class ChoiceRequestDTO {
		private String text;
	}
}
