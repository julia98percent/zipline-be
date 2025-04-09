package com.zipline.survey.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.User;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.SurveyNotFoundException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.util.S3FileUploader;
import com.zipline.repository.UserRepository;
import com.zipline.survey.dto.SurveyCreateRequestDTO;
import com.zipline.survey.dto.SurveyResponseDTO;
import com.zipline.survey.dto.SurveyResponseDetailDTO;
import com.zipline.survey.dto.SurveySubmitRequestDTO;
import com.zipline.survey.entity.Choice;
import com.zipline.survey.entity.Question;
import com.zipline.survey.entity.Survey;
import com.zipline.survey.entity.SurveyAnswer;
import com.zipline.survey.entity.SurveyResponse;
import com.zipline.survey.entity.enums.QuestionType;
import com.zipline.survey.entity.enums.SurveyStatus;
import com.zipline.survey.repository.QuestionRepository;
import com.zipline.survey.repository.SurveyAnswerRepository;
import com.zipline.survey.repository.SurveyRepository;
import com.zipline.survey.repository.SurveyResponseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final UserRepository userRepository;
	private final QuestionRepository questionRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;
	private final SurveyResponseRepository surveyResponseRepository;
	private final S3FileUploader s3FileUploader;
	private final FileQuestionMapper fileQuestionMapper;
	private final SurveyAnswerFactory surveyAnswerFactory;

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

	@Transactional
	public void createDefaultSurveyForUser(User user) {     //default questions
		Survey survey = new Survey("기본 설문지", user, SurveyStatus.ACTIVE, LocalDateTime.now(), null);

		Question nameQuestion = new Question(
			"이름",
			QuestionType.SUBJECTIVE,
			"고객님의 이름을 입력해주세요.",
			true,
			survey
		);

		Question phoneQuestion = new Question(
			"전화번호",
			QuestionType.SUBJECTIVE,
			"고객님의 전화번호를 입력해주세요.",
			true,
			survey
		);

		survey.getQuestions().add(nameQuestion);
		survey.getQuestions().add(phoneQuestion);

		surveyRepository.save(survey);
		user.setUrl(String.valueOf(survey.getUid()));
	}

	@Transactional(readOnly = true)
	public ApiResponse<SurveyResponseDTO> getSurvey(Long surveyUID) {
		Survey savedSurvey = surveyRepository.findByUidAndStatus(surveyUID, SurveyStatus.ACTIVE)
			.orElseThrow(() -> new SurveyNotFoundException("해당하는 설문이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
		List<Question> questions = questionRepository.findAllBySurveyUidWithChoices(savedSurvey.getUid());
		SurveyResponseDTO result = SurveyResponseDTO.from(savedSurvey, questions);
		return ApiResponse.ok("설문 조회 성공", result);
	}

	@Transactional
	public ApiResponse<Void> submitSurvey(Long surveyUid, List<SurveySubmitRequestDTO> requestDTOList,
		List<MultipartFile> files) {
		Survey savedSurvey = surveyRepository.findByUidAndStatus(surveyUid, SurveyStatus.ACTIVE)
			.orElseThrow(() -> new SurveyNotFoundException("해당하는 설문이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

		SurveyResponse surveyResponse = new SurveyResponse(savedSurvey, null, LocalDateTime.now());
		surveyResponseRepository.save(surveyResponse);
		List<Question> questions = questionRepository.findAllBySurveyUidWithChoices(savedSurvey.getUid());

		List<SurveyAnswer> generalAnswers = requestDTOList.stream()
			.map(dto -> surveyAnswerFactory.createAnswer(dto, questions, surveyResponse))
			.collect(Collectors.toList());

		Map<Long, MultipartFile> questionUidFileMap = fileQuestionMapper.mapFilesToQuestions(files, questions);
		Map<Long, String> questionUidUploadedUrlMap = s3FileUploader.uploadSurveyFiles(questionUidFileMap,
			S3Folder.SURVEYS);

		List<SurveyAnswer> fileAnswers = questionUidUploadedUrlMap.entrySet()
			.stream()
			.map(entry -> surveyAnswerFactory.createFileAnswer(entry.getKey(), entry.getValue(), questions,
				surveyResponse))
			.collect(Collectors.toList());

		List<SurveyAnswer> allAnswers = new ArrayList<>();
		allAnswers.addAll(generalAnswers);
		allAnswers.addAll(fileAnswers);
		surveyAnswerRepository.saveAll(allAnswers);
		return ApiResponse.create("설문 제출에 성공하였습니다.");
	}

	@Transactional(readOnly = true)
	public ApiResponse<SurveyResponseDetailDTO> getSubmittedSurvey(Long surveyResponseUid, Long userUid) {
		SurveyResponse savedSurveyResponse = surveyResponseRepository.findSurveyResponseWithSurveyAndUserById(
				surveyResponseUid)
			.orElseThrow(() -> new SurveyNotFoundException("해당하는 설문 응답이 존재하지 않습니다.", HttpStatus.BAD_REQUEST));

		if (!savedSurveyResponse.getSurvey().getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findByWithQuestionsAndChoicesSurveyResponseUid(
			savedSurveyResponse.getUid());
		SurveyResponseDetailDTO responseDTO = new SurveyResponseDetailDTO(savedSurveyResponse, surveyAnswers);

		return ApiResponse.ok("설문 상세조회에 성공하였습니다.", responseDTO);
	}
}