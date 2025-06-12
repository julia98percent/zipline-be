package com.zipline.service.label.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LabelListResponseDTO {
	private List<LabelResponseDTO> labels;

	public LabelListResponseDTO(List<LabelResponseDTO> labels) {
		this.labels = labels;
	}

}