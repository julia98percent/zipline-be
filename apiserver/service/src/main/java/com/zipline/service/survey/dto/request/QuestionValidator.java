package com.zipline.service.survey.dto.request;

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

	private static final String TITLE_MIN_LENGTH_ERROR_MSG = "문항 제목의 최소 길이는 1자 입니다.";
	private static final String TITLE_MAX_LENGTH_ERROR_MSG = "문항 제목의 최대 길이는 20자 입니다.";
	private static final String TYPE_REQUIRED_ERROR_MSG = "문항의 타입은 필수입니다.";
	private static final String IS_REQUIRED_ERROR_MSG = "문항의 required 여부는 필수값입니다.";
	private static final String INVALID_TYPE_ERROR_MSG = "유효하지 않은 문항 타입입니다";
	private static final String DESCRIPTION_MIN_LENGTH_ERROR_MSG = "문항 설명의 최소 길이는 1자 입니다.";
	private static final String DESCRIPTION_MAX_LENGTH_ERROR_MSG = "문항 설명의 최대 길이는 %d자 입니다.";
	private static final String CHOICE_REQUIRED_ERROR_MSG = "객관식 문항은 최소 1개의 선택지를 포함해야 합니다.";
	private static final String CHOICE_NOT_ALLOWED_ERROR_MSG = "객관식이 아닌 문항에는 선택지를 추가할 수 없습니다.";
	private static final String MULTIPLE_CHOICE_MIN_COUNT_ERROR_MSG = "객관식 복수 선택 문항은 최소 3개의 선택지를 포함해야 합니다.";

	private static final int TITLE_MAX_LENGTH = 20;
	private static final int CHOICE_DESCRIPTION_MAX_LENGTH = 30;
	private static final int SUBJECTIVE_DESCRIPTION_MAX_LENGTH = 200;
	private static final int MULTIPLE_CHOICE_MIN_CHOICES_COUNT = 3;

	@Override
	public boolean isValid(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext constraintValidatorContext) {

		return validateTitle(questionRequestDTO, constraintValidatorContext)
			&& validateType(questionRequestDTO, constraintValidatorContext)
			&& validateIsRequired(questionRequestDTO, constraintValidatorContext)
			&& validateQuestionType(questionRequestDTO, constraintValidatorContext)
			&& validateDescription(questionRequestDTO, constraintValidatorContext)
			&& validateChoices(questionRequestDTO, constraintValidatorContext);
	}

	private boolean validateTitle(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		if (questionRequestDTO.getTitle() == null || questionRequestDTO.getTitle().trim().isEmpty()) {
			addConstraintViolation(context, TITLE_MIN_LENGTH_ERROR_MSG);
			return false;
		}
		if (questionRequestDTO.getTitle().length() > TITLE_MAX_LENGTH) {
			addConstraintViolation(context, TITLE_MAX_LENGTH_ERROR_MSG);
			return false;
		}
		return true;
	}

	private boolean validateType(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		if (questionRequestDTO.getType() == null || questionRequestDTO.getType().trim().isEmpty()) {
			addConstraintViolation(context, TYPE_REQUIRED_ERROR_MSG);
			return false;
		}
		return true;
	}

	private boolean validateIsRequired(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		if (questionRequestDTO.getIsRequired() == null) {
			addConstraintViolation(context, IS_REQUIRED_ERROR_MSG);
			return false;
		}
		return true;
	}

	private boolean validateQuestionType(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		if (!QUESTION_TYPES_SET.contains(questionRequestDTO.getType())) {
			addConstraintViolation(context, INVALID_TYPE_ERROR_MSG);
			return false;
		}
		return true;
	}

	private boolean validateDescription(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		if (questionRequestDTO.getDescription() == null || questionRequestDTO.getDescription().trim().isEmpty()) {
			addConstraintViolation(context, DESCRIPTION_MIN_LENGTH_ERROR_MSG);
			return false;
		}

		int maxLength = getMaxLengthByType(questionRequestDTO.getType());
		if (questionRequestDTO.getDescription().length() > maxLength) {
			addConstraintViolation(context, String.format(DESCRIPTION_MAX_LENGTH_ERROR_MSG, maxLength));
			return false;
		}
		return true;
	}

	private boolean validateChoices(SurveyCreateRequestDTO.QuestionRequestDTO questionRequestDTO,
		ConstraintValidatorContext context) {
		String type = questionRequestDTO.getType();

		if (isChoiceQuestion(type)) {
			if (questionRequestDTO.getChoices().isEmpty()) {
				addConstraintViolation(context, CHOICE_REQUIRED_ERROR_MSG);
				return false;
			}

			if (isMultipleChoiceQuestion(type)
				&& questionRequestDTO.getChoices().size() < MULTIPLE_CHOICE_MIN_CHOICES_COUNT) {
				addConstraintViolation(context, MULTIPLE_CHOICE_MIN_COUNT_ERROR_MSG);
				return false;
			}
		} else if (!questionRequestDTO.getChoices().isEmpty()) {
			addConstraintViolation(context, CHOICE_NOT_ALLOWED_ERROR_MSG);
			return false;
		}
		return true;
	}

	private void addConstraintViolation(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}

	private int getMaxLengthByType(String questionType) {
		if (isChoiceQuestion(questionType)) {
			return CHOICE_DESCRIPTION_MAX_LENGTH;
		}
		return SUBJECTIVE_DESCRIPTION_MAX_LENGTH;
	}

	private boolean isChoiceQuestion(String questionType) {
		return QuestionType.SINGLE_CHOICE.name().equals(questionType) || QuestionType.MULTIPLE_CHOICE.name()
			.equals(questionType);
	}

	private boolean isMultipleChoiceQuestion(String questionType) {
		return QuestionType.MULTIPLE_CHOICE.name().equals(questionType);
	}
}
