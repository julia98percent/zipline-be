package com.zipline.service.label.dto.request;

import com.zipline.entity.label.Label;
import com.zipline.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LabelRequestDTO {

	@Schema(description = "라벨 이름", example = "VIP 고객")
	@NotBlank(message = "라벨 이름은 필수입니다.")
	private String name;

	public Label toEntity(User user) {
		return new Label(user, name);
	}
}