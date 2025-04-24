package com.zipline.service.label.dto.request;

import com.zipline.entity.label.Label;
import com.zipline.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LabelRequestDTO {

	@Schema(description = "라벨 이름", example = "중요")
	@NotBlank(message = "라벨명은 필수 입력값입니다.")
	@Size(min = 1, max = 10, message = "라벨명은 1자 이상 10자 이하로 입력해주세요.")
	@Pattern(regexp = "^[a-zA-Z가-힣0-9\\s]+$", message = "라벨명에는 특수문자를 포함할 수 없습니다.")
	private String name;

	public Label toEntity(User user) {
		return new Label(user, name);
	}
}