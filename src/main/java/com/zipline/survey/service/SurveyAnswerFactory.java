package com.zipline.survey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.zipline.global.exception.custom.QuestionNotFoundException;
import com.zipline.global.exception.custom.QuestionTypeException;
import com.zipline.survey.dto.SurveySubmitRequestDTO;
import com.zipline.survey.entity.Question;
import com.zipline.survey.entity.SurveyAnswer;
import com.zipline.survey.entity.SurveyResponse;
import com.zipline.survey.entity.enums.QuestionType;

@Component
public class SurveyAnswerFactory {

	public SurveyAnswer createAnswer(SurveySubmitRequestDTO requestDTO, List<Question> questions,
		SurveyResponse surveyResponse) {
		Question question = findQuestionByUid(questions, requestDTO.getQuestionId());

		if (question.getQuestionType() == QuestionType.SUBJECTIVE) {
			return new SurveyAnswer(surveyResponse, question, requestDTO.getAnswer());
		}
		if (isChoiceQuestion(question.getQuestionType())) {
			String choiceValue = String.join(",", requestDTO.getChoiceIds().stream()
				.map(String::valueOf)
				.collect(Collectors.toList()));
			return new SurveyAnswer(surveyResponse, question, choiceValue);
		}
		throw new QuestionTypeException("지원하지 않는 질문 타입입니다.", HttpStatus.BAD_REQUEST);
	}

	public SurveyAnswer createFileAnswer(Long questionUid, String fileUrl, List<Question> questions,
		SurveyResponse response) {
		Question question = findQuestionByUid(questions, questionUid);
		return new SurveyAnswer(response, question, fileUrl);
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
}
