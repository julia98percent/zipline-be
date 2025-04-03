package com.zipline.survey.dto;

import com.zipline.survey.entity.Choice;

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
