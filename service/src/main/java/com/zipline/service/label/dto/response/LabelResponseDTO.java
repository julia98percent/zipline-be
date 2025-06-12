package com.zipline.service.label.dto.response;

import com.zipline.entity.label.Label;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LabelResponseDTO {
	private Long uid;
	private String name;

	public LabelResponseDTO(Label label) {
		this.uid = label.getUid();
		this.name = label.getName();
	}
}