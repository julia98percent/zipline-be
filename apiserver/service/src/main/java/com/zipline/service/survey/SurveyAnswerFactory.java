package com.zipline.service.survey;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.entity.enums.QuestionType;
import com.zipline.entity.survey.Choice;
import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.global.exception.custom.ChoiceNotAllowedException;
import com.zipline.global.exception.custom.QuestionNotFoundException;
import com.zipline.global.exception.custom.QuestionTypeException;

@Component
public class SurveyAnswerFactory {

	public SurveyAnswer createAnswer(SurveySubmitRequestDTO requestDTO, List<Question> questions,
		SurveyResponse surveyResponse) {
		Question question = findQuestionByUid(questions, requestDTO.getQuestionId());

		if (question.getQuestionType() == QuestionType.SUBJECTIVE) {
			return new SurveyAnswer(surveyResponse, question, requestDTO.getAnswer(), null);
		}

		if (isChoiceQuestion(question.getQuestionType())) {
			validateChoicesIds(question, requestDTO.getChoiceIds());
			String choiceValue = String.join(",", requestDTO.getChoiceIds().stream()
				.map(String::valueOf)
				.collect(Collectors.toList()));
			return new SurveyAnswer(surveyResponse, question, choiceValue, null);
		}
		throw new QuestionTypeException("지원하지 않는 질문 타입입니다.", HttpStatus.BAD_REQUEST);
	}

	public SurveyAnswer createFileAnswer(Long questionUid, String fileUrl, List<Question> questions,
		SurveyResponse response) {
		Question question = findQuestionByUid(questions, questionUid);
		return new SurveyAnswer(response, question, fileUrl, null);
	}

	private Question findQuestionByUid(List<Question> questions, Long uid) {
		return questions.stream()
			.filter(q -> q.getUid().equals(uid))
			.findFirst()
			.orElseThrow(() -> new QuestionNotFoundException("해당하는 문항이 없습니다.", HttpStatus.BAD_REQUEST));
	}

	private boolean isChoiceQuestion(QuestionType questionType) {
		return questionType == QuestionType.MULTIPLE_CHOICE || questionType == QuestionType.SINGLE_CHOICE;
	}

	private void validateChoicesIds(Question question, List<Long> submittedChoiceIds) {
		if (question.getQuestionType() == QuestionType.SINGLE_CHOICE && submittedChoiceIds.size() > 1) {
			throw new ChoiceNotAllowedException("단일 선택 문항에는 하나의 선택지만 허용됩니다.", HttpStatus.BAD_REQUEST);
		}
		if (!question.getChoices().stream()
			.map(Choice::getUid)
			.collect(Collectors.toSet())
			.containsAll(submittedChoiceIds)) {
			throw new ChoiceNotAllowedException("올바르지 않은 선택지가 포함되어 있습니다.", HttpStatus.BAD_REQUEST);
		}
	}
}
