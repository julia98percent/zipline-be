package com.zipline.dto.survey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.zipline.entity.enums.QuestionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuestionValidator
	implements ConstraintValidator<QuestionValidate, SurveyCreateRequestDTO.QuestionRequestDTO> {

	private static final HashSet<String> QUESTION_TYPES_SET = Arrays.stream(QuestionType.values())
		.map(Enum::name)
		.collect(Collectors.toCollection(HashSet::new));

	@Override
	public boolean isValid(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext constraintValidatorContext) {

		if (!QUESTION_TYPES_SET.contains(questionRequestDTO.getType())) {
			constraintValidatorContext.disableDefaultConstraintViolation();
			constraintValidatorContext.buildConstraintViolationWithTemplate("유효하지 않은 문항 타입입니다")
				.addConstraintViolation();
			return false;
		}

		int max = getMaxLengthByType(questionRequestDTO.getType());
		if (questionRequestDTO.getDescription().length() > max) {
			constraintValidatorContext.disableDefaultConstraintViolation();
			constraintValidatorContext.buildConstraintViolationWithTemplate("문항 설명의 최대 길이는 " + max + "자 입니다.")
				.addConstraintViolation();
			return false;
		}

		// 객관식 질문 (선택지 필수)
		if (isChoiceQuestion(questionRequestDTO.getType())) {
			if (questionRequestDTO.getChoices().isEmpty()) {
				constraintValidatorContext.disableDefaultConstraintViolation();
				constraintValidatorContext.buildConstraintViolationWithTemplate("객관식 문항은 최소 1개의 선택지를 포함해야 합니다.")
					.addConstraintViolation();
				return false;
			}
		}

		if (!isChoiceQuestion(questionRequestDTO.getType())) {
			if (!questionRequestDTO.getChoices().isEmpty()) {
				constraintValidatorContext.disableDefaultConstraintViolation();
				constraintValidatorContext.buildConstraintViolationWithTemplate("객관식이 아닌 문항에는 선택지를 추가할 수 없습니다.")
					.addConstraintViolation();
				return false;
			}
		}

		if (isMultipleChoiceQuestion(questionRequestDTO.getType())) {
			if (questionRequestDTO.getChoices().size() < 3) {
				constraintValidatorContext.disableDefaultConstraintViolation();
				constraintValidatorContext.buildConstraintViolationWithTemplate("객관식 복수 선택 문항은 최소 3개의 선택지를 포함해야 합니다.")
					.addConstraintViolation();
				return false;
			}
		}
		return true;
	}

	private int getMaxLengthByType(String questionType) {
		if (isChoiceQuestion(questionType)) {
			return 30;
		}
		return 200;
	}

	private boolean isChoiceQuestion(String questionType) {
		if (questionType.equals("MULTIPLE_CHOICE") || questionType.equals("SINGLE_CHOICE")) {
			return true;
		}
		return false;
	}

	private boolean isMultipleChoiceQuestion(String questionType) {
		if (questionType.equals("MULTIPLE_CHOICE")) {
			return true;
		}
		return false;
	}
}
