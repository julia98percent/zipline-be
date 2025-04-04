package com.zipline.survey.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.User;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.SurveyNotFoundException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.repository.UserRepository;
import com.zipline.survey.dto.SurveyCreateRequestDTO;
import com.zipline.survey.dto.SurveyResponseDTO;
import com.zipline.survey.entity.Choice;
import com.zipline.survey.entity.Question;
import com.zipline.survey.entity.Survey;
import com.zipline.survey.entity.enums.QuestionType;
import com.zipline.survey.entity.enums.SurveyStatus;
import com.zipline.survey.repository.QuestionRepository;
import com.zipline.survey.repository.SurveyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final UserRepository userRepository;
	private final QuestionRepository questionRepository;

	@Transactional
	public ApiResponse<Map<String, Long>> createSurvey(SurveyCreateRequestDTO requestDTO, Long agentUID) {
		User user = userRepository.findById(agentUID)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		Survey survey = new Survey(requestDTO.getTitle(), user, SurveyStatus.ACTIVE, LocalDateTime.now(), null);

		requestDTO.getQuestions().forEach(questionDTO -> {
			Question question = new Question(questionDTO.getTitle(), QuestionType.valueOf(questionDTO.getType()),
				questionDTO.getDescription(), questionDTO.getIsRequired(), survey);

			questionDTO.getChoices().forEach(choiceDTO -> {
				Choice choice = new Choice(choiceDTO.getContent(), question);
				question.addChoice(choice);
			});
			survey.getQuestions().add(question);
		});
		surveyRepository.save(survey);
		user.setUrl(String.valueOf(survey.getUid()));
		return ApiResponse.create("설문 등록 완료", Collections.singletonMap("surveyURL", survey.getUid()));
	}

	@Transactional(readOnly = true)
	public ApiResponse<SurveyResponseDTO> getSurvey(Long surveyUID) {
		Survey savedSurvey = surveyRepository.findByUidAndStatus(surveyUID, SurveyStatus.ACTIVE)
			.orElseThrow(() -> new SurveyNotFoundException("해당하는 설문이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
		List<Question> questions = questionRepository.findAllBySurveyUidWithChoices(savedSurvey.getUid());
		SurveyResponseDTO result = SurveyResponseDTO.from(savedSurvey, questions);
		return ApiResponse.ok("설문 조회 성공", result);
	}
}
