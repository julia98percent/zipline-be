package com.zipline.service.survey.dto.request;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Tag(name = "설문 생성 요청", description = "설문을 생성하기 위한 요청 DTO")
@Getter
public class SurveyCreateRequestDTO {

	@NotBlank(message = "설문 제목의 최소 길이는 1자 입니다.")
	@Size(max = 20, message = "설문 제목의 최대 길이는 20자 입니다.")
	private String title;

	@ArraySchema(schema = @Schema(description = "설문의 문항 목록", implementation = QuestionRequestDTO.class))
	@NotEmpty(message = "설문에는 최소 한 개의 문항이 필요합니다.")
	@Size(max = 20, message = "설문에는 최대 20개의 문항만 포함할수 있습니다.")
	private List<@Valid QuestionRequestDTO> questions = new ArrayList<>();

	@Schema(name = "QuestionRequestDTO", description = "설문 문항을 생성하기 위한 요청 DTO")
	@Getter
	@QuestionValidate
	public static class QuestionRequestDTO {
		@Schema(description = "문항 제목", example = "관심 매물의 종류")
		private String title;

		@Schema(description = "문항 설명", example = "선택지 중 하나를 골라주세요.")
		private String description;

		@Schema(description = "문항 타입", example = "SINGLE_CHOICE", allowableValues = {"SINGLE_CHOICE", "MULTIPLE_CHOICE",
			"SUBJECTIVE", "FILE_UPLOAD"})
		private String type;

		@Schema(description = "필수 응답 여부", example = "true")
		private Boolean isRequired;

		@ArraySchema(schema = @Schema(description = "문항 선택지 목록", implementation = ChoiceRequestDTO.class))
		private List<@Valid ChoiceRequestDTO> choices = new ArrayList<>();
	}

	@Getter
	public static class ChoiceRequestDTO {
		@Size(max = 30, message = "선택지 내용의 최대 길이는 30자 입니다.")
		@NotBlank(message = "선택지 내용의 최소 길이는 1자입니다.")
		@Schema(description = "선택지 내용", example = "월세")
		private String content;
	}
}
