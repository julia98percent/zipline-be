package com.zipline.survey.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.zipline.survey.entity.Choice;
import com.zipline.survey.entity.SurveyAnswer;
import com.zipline.survey.entity.SurveyResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "설문 응답 상세 DTO")
@Getter
public class SurveyResponseDetailDTO {

	@Schema(description = "설문 응답 UID", example = "7")
	private Long surveyResponseUid;
	@Schema(description = "설문 제목", example = "설문 제목입니다.")
	private String title;
	@Schema(description = "설문 제출 시간", example = "2025-04-08T14:59:15.694635")
	private LocalDateTime submittedAt;
	@Schema(description = "고객 UID (신규 제출일 경우 null)", example = "101")
	private Long customerUid;
	@Schema(description = "문항별 응답 리스트")
	private List<SurveyAnswerResponseDTO> answers;

	public SurveyResponseDetailDTO(SurveyResponse surveyResponse, List<SurveyAnswer> surveyAnswers) {
		this.surveyResponseUid = surveyResponse.getUid();
		this.title = surveyResponse.getSurvey().getTitle();
		this.submittedAt = surveyResponse.getCreatedAt();
		this.customerUid = surveyResponse.getCustomer() == null ? null : surveyResponse.getCustomer().getUid();
		this.answers = surveyAnswers.stream().map(SurveyAnswerResponseDTO::new).collect(Collectors.toList());
	}

	@Schema(description = "개별 문항에 대한 응답 정보")
	@Getter
	public static class SurveyAnswerResponseDTO {
		@Schema(description = "문항 UID", example = "13")
		private Long questionUid;
		@Schema(description = "문항 제목", example = "관심 매물 조사")
		private String questionTitle;
		@Schema(description = "문항 설명", example = "어떤 종류의 매물을 찾으시나요?(월세, 전세, 매매)")
		private String description;
		@Schema(description = "필수 응답 여부", example = "true")
		private boolean isRequired;
		@Schema(description = "질문 타입", example = "SUBJECTIVE")
		private String questionType;
		@Schema(description = "응답 내용 (텍스트/선택지 ID)", example = "주관식: 주관식 답변, 파일업로드: 이미지 URL, 객관식(단일): choiceUid, 객관식(다중): choiceUid1,choiceUid2")
		private String answer;
		@Schema(description = "선택지 리스트")
		private List<AnswerChoiceResponseDTO> choices;

		public SurveyAnswerResponseDTO(SurveyAnswer surveyAnswer) {
			this.questionUid = surveyAnswer.getQuestion().getUid();
			this.questionTitle = surveyAnswer.getQuestion().getTitle();
			this.description = surveyAnswer.getQuestion().getDescription();
			this.isRequired = surveyAnswer.getQuestion().isRequired();
			this.questionType = surveyAnswer.getQuestion().getQuestionType().name();
			this.answer = surveyAnswer.getAnswer();
			this.choices = surveyAnswer.getQuestion().getChoices().stream().map(AnswerChoiceResponseDTO::new).collect(
				Collectors.toList());
		}
	}

	@Schema(description = "문항과 연결된 선택지 정보 (주관식 및 파일업로드 타입일 시 빈 배열)")
	@Getter
	public static class AnswerChoiceResponseDTO {
		@Schema(description = "선택지 UID", example = "19")
		private Long choiceUid;
		@Schema(description = "선택지 텍스트", example = "월세")
		private String choiceText;

		public AnswerChoiceResponseDTO(Choice choice) {
			this.choiceUid = choice.getUid();
			this.choiceText = choice.getText();
		}
	}
}
