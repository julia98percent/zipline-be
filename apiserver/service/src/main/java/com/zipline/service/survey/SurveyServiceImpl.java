package com.zipline.service.survey;

import static com.zipline.entity.enums.NotificationCategory.NEW_SURVEY;

import com.zipline.entity.enums.QuestionType;
import com.zipline.entity.notification.Notification;
import com.zipline.entity.survey.Choice;
import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.Survey;
import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.entity.user.User;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.survey.SurveyException;
import com.zipline.global.exception.survey.errorcode.SurveyErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.request.SurveyFileDTO;
import com.zipline.global.util.S3FileUploader;
import com.zipline.repository.notification.NotificationRepository;
import com.zipline.repository.survey.QuestionRepository;
import com.zipline.repository.survey.SurveyAnswerRepository;
import com.zipline.repository.survey.SurveyRepository;
import com.zipline.repository.survey.SurveyResponseRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.notification.EmitterService;
import com.zipline.service.notification.dto.response.NotificationResponseDTO;
import com.zipline.service.survey.dto.request.SurveyCreateRequestDTO;
import com.zipline.service.survey.dto.request.SurveySubmitRequestDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDTO;
import com.zipline.service.survey.dto.response.SurveyResponseDetailDTO;
import com.zipline.service.survey.dto.response.SurveyResponseListDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class SurveyServiceImpl implements SurveyService {

  private final SurveyRepository surveyRepository;
  private final UserRepository userRepository;
  private final QuestionRepository questionRepository;
  private final SurveyAnswerRepository surveyAnswerRepository;
  private final SurveyResponseRepository surveyResponseRepository;
  private final EmitterService emitterService;

  private final S3FileUploader s3FileUploader;
  private final FileQuestionMapper fileQuestionMapper;
  private final SurveyAnswerFactory surveyAnswerFactory;
  private final NotificationRepository notificationRepository;

  @Transactional
  public Map<String, String> createSurvey(SurveyCreateRequestDTO requestDTO, Long userUid) {
    User user = userRepository.findById(userUid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    List<Survey> findSurveys = surveyRepository.findByUserUidAndDeletedAtIsNull(userUid);
    LocalDateTime now = LocalDateTime.now();
    findSurveys.forEach(survey -> {
      survey.delete(now);
    });

    Survey survey = new Survey(requestDTO.getTitle(), user);

    requestDTO.getQuestions().forEach(questionDTO -> {
      Question question = new Question(questionDTO.getTitle(),
          QuestionType.valueOf(questionDTO.getType()),
          questionDTO.getDescription(), questionDTO.getIsRequired(), survey);

      questionDTO.getChoices().forEach(choiceDTO -> {
        Choice choice = new Choice(choiceDTO.getContent(), question);
        question.addChoice(choice);
      });
      survey.getQuestions().add(question);
    });
    surveyRepository.save(survey);
    user.setUrl(String.valueOf(survey.getUlid()));
    return Collections.singletonMap("surveyURL", survey.getUlid());
  }

  @Transactional
  public void createDefaultSurveyForUser(User user) {
    Survey survey = new Survey("기본 설문지", user);

    Question nameQuestion = new Question(
        "이름",
        QuestionType.SUBJECTIVE,
        "고객님의 이름을 입력해주세요.",
        false,
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
    user.setUrl(String.valueOf(survey.getUlid()));
  }

  @Transactional(readOnly = true)
  public SurveyResponseDTO getSurvey(String surveyUlid) {
    Survey savedSurvey = surveyRepository.findByUlidAndDeleteAtIsNull(surveyUlid)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    List<Question> questions = questionRepository.findAllBySurveyUidWithChoices(
        savedSurvey.getUid());
    return SurveyResponseDTO.from(savedSurvey, questions);
  }

  @Transactional
  public void submitSurvey(String surveyUlid, List<SurveySubmitRequestDTO> requestDTOList,
      List<MultipartFile> files) {
    Survey savedSurvey = surveyRepository.findByUlidAndDeleteAtIsNull(surveyUlid)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

    SurveyResponse surveyResponse = new SurveyResponse(savedSurvey, null, LocalDateTime.now());
    surveyResponseRepository.save(surveyResponse);
    List<Question> questions = questionRepository.findAllBySurveyUidWithChoices(
        savedSurvey.getUid());

    List<SurveyAnswer> generalAnswers = requestDTOList.stream()
        .map(dto -> surveyAnswerFactory.createAnswer(dto, questions, surveyResponse))
        .collect(Collectors.toList());

    List<SurveyAnswer> fileAnswers = handleFileAnswers(files, questions, surveyResponse);

    List<SurveyAnswer> allAnswers = new ArrayList<>();
    allAnswers.addAll(generalAnswers);
    allAnswers.addAll(fileAnswers);
    surveyAnswerRepository.saveAll(allAnswers);

    User user = surveyResponseRepository.findUserBySurveyUlid(surveyUlid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    String customerName = !generalAnswers.isEmpty() ? generalAnswers.get(0).getAnswer() : "-";
    String customerPhoneNo = generalAnswers.size() > 1 ? generalAnswers.get(1).getAnswer() : "-";
    String notificationMessage = String.format("신규 고객이 상담을 요청했습니다. - 이름: %s / 전화번호: %s",
        customerName,
        customerPhoneNo);

    Notification notification = new Notification(NEW_SURVEY, notificationMessage, user, null,
        surveyResponse);
    NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.from(notification);
    notificationRepository.save(notification);
    emitterService.notify(notificationResponseDTO);

  }

  @Transactional(readOnly = true)
  public SurveyResponseListDTO getSurveyResponses(PageRequestDTO pageRequestDTO, Long userUid) {
    Page<SurveyResponse> savedSurveyResponses = surveyResponseRepository.findBySurveyUserUid(
        userUid,
        pageRequestDTO.toPageable());

    List<Long> savedSurveyResponsesIds = savedSurveyResponses.getContent().stream()
        .map(SurveyResponse::getUid)
        .collect(Collectors.toList());

    List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findTop2ByResponseIdIn(
        savedSurveyResponsesIds);
    Map<Long, List<SurveyAnswer>> groupedSurveyAnswers = surveyAnswers.stream()
        .collect(Collectors.groupingBy(sa -> sa.getSurveyResponse().getUid()));

    List<SurveyResponseListDTO.SurveyResponseListDataDTO> surveyResponseListDataDTOs = new ArrayList<>();
    for (Long surveyResponseUid : groupedSurveyAnswers.keySet()) {
      List<SurveyAnswer> answers = groupedSurveyAnswers.get(surveyResponseUid)
          .stream()
          .sorted(Comparator.comparing(surveyAnswer -> surveyAnswer.getUid()))
          .collect(Collectors.toList());

      surveyResponseListDataDTOs.add(
          new SurveyResponseListDTO.SurveyResponseListDataDTO(surveyResponseUid,
              answers.get(0).getAnswer(),
              answers.get(1).getAnswer(),
              answers.get(0).getSurveyResponse().getCreatedAt()));
    }

    surveyResponseListDataDTOs.sort(
        Comparator.comparing(SurveyResponseListDTO.SurveyResponseListDataDTO::getSubmittedAt,
            Comparator.reverseOrder()));

    return new SurveyResponseListDTO(surveyResponseListDataDTOs, savedSurveyResponses);
  }

  @Transactional(readOnly = true)
  public SurveyResponseDetailDTO getSubmittedSurvey(Long surveyResponseUid, Long userUid) {
    SurveyResponse savedSurveyResponse = surveyResponseRepository.findSurveyResponseWithSurveyAndUserById(
            surveyResponseUid)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

    if (!savedSurveyResponse.getSurvey().getUser().getUid().equals(userUid)) {
      throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
    }

    List<SurveyAnswer> surveyAnswers = surveyAnswerRepository.findByWithQuestionsAndChoicesSurveyResponseUid(
        savedSurveyResponse.getUid());
    return new SurveyResponseDetailDTO(savedSurveyResponse, surveyAnswers);
  }

  private List<SurveyAnswer> handleFileAnswers(List<MultipartFile> files, List<Question> questions,
      SurveyResponse surveyResponse) {

    if (files == null || files.isEmpty()) {
      return Collections.emptyList();
    }

    Map<Long, SurveyFileDTO> questionUidSurveyFileVOMap = fileQuestionMapper.mapFilesToQuestions(
        files, questions);
    Map<Long, SurveyFileDTO> questionUidUploadedUrlMap = s3FileUploader.uploadSurveyFiles(
        questionUidSurveyFileVOMap,
        S3Folder.SURVEYS);

    List<SurveyAnswer> fileAnswers = questionUidUploadedUrlMap.entrySet()
        .stream()
        .map(entry -> surveyAnswerFactory.createFileAnswer(entry.getKey(),
            entry.getValue().getUploadedUrl(),
            questions,
            surveyResponse, entry.getValue().getFileName()))
        .collect(Collectors.toList());
    return fileAnswers;
  }
}