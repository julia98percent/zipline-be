package com.zipline.service.survey.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class SurveySubmitRequestDTO {
	private Long questionId;
	private List<Long> choiceIds;
	private String answer;
}
