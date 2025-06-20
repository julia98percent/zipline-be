package com.zipline.service.survey.dto.response;

import com.zipline.entity.survey.Choice;

import lombok.Getter;

@Getter
public class ChoiceResponseDTO {
	private Long id;
	private String text;

	public ChoiceResponseDTO(Choice choice) {
		this.id = choice.getUid();
		this.text = choice.getText();
	}
}
