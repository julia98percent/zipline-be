package com.zipline.service.user;

import com.zipline.entity.survey.Survey;
import com.zipline.entity.user.Authority;
import com.zipline.entity.user.PasswordQuestion;
import com.zipline.entity.user.PasswordQuestionAnswer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.survey.SurveyException;
import com.zipline.global.exception.survey.errorcode.SurveyErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.survey.SurveyRepository;
import com.zipline.repository.user.PasswordQuestionAnswerRepository;
import com.zipline.repository.user.PasswordQuestionRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.survey.SurveyService;
import com.zipline.service.user.dto.request.FindPasswordRequestDTO;
import com.zipline.service.user.dto.request.FindUserIdRequestDTO;
import com.zipline.service.user.dto.request.LoginRequestDTO;
import com.zipline.service.user.dto.request.ResetPasswordRequestDTO;
import com.zipline.service.user.dto.request.SignUpRequestDTO;
import com.zipline.service.user.dto.request.UserModifyRequestDTO;
import com.zipline.service.user.dto.response.FindUserIdResponseDTO;
import com.zipline.service.user.dto.response.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  @Transactional(readOnly = true)
  public UserResponseDTO findById(Long uid) {
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

    return UserResponseDTO.userSurvey(user, survey);
  }

  private final UserRepository userRepository;
  private final SurveyRepository surveyRepository;
  private final PasswordQuestionRepository passwordQuestionRepository;
  private final PasswordQuestionAnswerRepository passwordQuestionAnswerRepository;
  private final PasswordEncoder passwordEncoder;
  private final RedisTemplate<String, String> redisTemplate;
  private final SurveyService surveyService;
  private final AuthenticationManager authenticationManager;

  @Transactional(readOnly = true)
  public UserResponseDTO findById(String id) {
    User user = userRepository.findByLoginId(id)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

    return UserResponseDTO.userSurvey(user, survey);
  }

  @Transactional
  public void signup(SignUpRequestDTO signUpRequestDto) {

    if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getPasswordCheck())) {
      throw new UserException(UserErrorCode.INVALID_PASSWORD_CHECK);
    }

    if (userRepository.existsById(signUpRequestDto.getId())) {
      throw new UserException(UserErrorCode.INVALID_USER_ID);
    }

    PasswordQuestion question = passwordQuestionRepository.findById(
            signUpRequestDto.getPasswordQuestionUid())
        .orElseThrow(() -> new UserException(UserErrorCode.PASSWORD_QUESTION_NOT_FOUND));

    User user = User.builder()
        .id(signUpRequestDto.getId())
        .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
        .name(signUpRequestDto.getName())
        .phoneNo(signUpRequestDto.getPhoneNo())
        .email(signUpRequestDto.getEmail())
        .role(Authority.ROLE_AGENT)
        .noticeMonth(3)
        .noticeTime(LocalTime.of(11, 0))
        .build();
    surveyService.createDefaultSurveyForUser(user);
    userRepository.save(user);

    PasswordQuestionAnswer passwordQuestionAnswer = PasswordQuestionAnswer.create(user, question,
        signUpRequestDto.getQuestionAnswer());
    passwordQuestionAnswerRepository.save(passwordQuestionAnswer);

  }

  @Transactional
  public UserResponseDTO authenticateAndLogin(LoginRequestDTO loginRequestDTO,
      HttpServletRequest request) {
    User user = userRepository.findByLoginId(loginRequestDTO.getId())
        .orElseThrow(() -> new UserException(UserErrorCode.INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
      throw new UserException(UserErrorCode.INVALID_CREDENTIALS);
    }

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequestDTO.getId(),
            loginRequestDTO.getPassword());

    Authentication authentication = authenticationManager.authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    HttpSession session = request.getSession(true);

    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
 
    Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    
    return UserResponseDTO.userSurvey(user, survey);
  }

  public UserResponseDTO updateInfo(Long uid, UserModifyRequestDTO userModifyRequestDto) {
    User user = userRepository.findById(uid)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
        .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));
    user.updateInfo(
        userModifyRequestDto.getName(),
        userModifyRequestDto.getUrl(),
        userModifyRequestDto.getPhoneNo(),
        userModifyRequestDto.getEmail(),
        userModifyRequestDto.getNoticeMonth(),
        userModifyRequestDto.getNoticeTime()
    );

    userRepository.save(user);
    return UserResponseDTO.userSurvey(user, survey);
  }

  @Transactional(readOnly = true)
  public FindUserIdResponseDTO findUserId(FindUserIdRequestDTO findUserIdRequestDto) {
    User user = userRepository.findByNameAndEmail(
        findUserIdRequestDto.getName(),
        findUserIdRequestDto.getEmail()
    ).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    return new FindUserIdResponseDTO(user.getId());
  }

  @Transactional
  public String findUserPassword(FindPasswordRequestDTO findPasswordRequestDTO) {
    User user = userRepository.findByLoginId(findPasswordRequestDTO.getLoginId())
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    PasswordQuestion question = passwordQuestionRepository.findById(
            findPasswordRequestDTO.getPasswordQuestionUid())
        .orElseThrow(() -> new UserException(UserErrorCode.PASSWORD_QUESTION_NOT_FOUND));

    PasswordQuestionAnswer answer = passwordQuestionAnswerRepository.findByUserAndPasswordQuestion(
            user, question)
        .orElseThrow(() -> new UserException(UserErrorCode.PASSWORD_QUESTION_NOT_FOUND));

    if (!answer.getAnswer().equals(findPasswordRequestDTO.getAnswer())) {
      throw new UserException(UserErrorCode.INCORRECT_QUESTION_ANSWER);
    }

    Set<String> keys = redisTemplate.keys("resetToken:*");
    if (keys != null) {
      for (String key : keys) {
        String uidStr = redisTemplate.opsForValue().get(key);
        if (uidStr != null && uidStr.equals(user.getUid().toString())) {
          redisTemplate.delete(key);
        }
      }
    }

    String resetToken = UUID.randomUUID().toString();

    redisTemplate.opsForValue()
        .set("resetToken:" + resetToken, user.getUid().toString(),
            Duration.ofMinutes(5));   //비밀번호 재설정 제한시간 5분

    return resetToken;
  }

  @Transactional
  public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
    if (!resetPasswordRequestDTO.getNewPassword()
        .equals(resetPasswordRequestDTO.getNewPasswordCheck())) {
      throw new UserException(UserErrorCode.INVALID_PASSWORD_CHECK);
    }

    String resetToken = resetPasswordRequestDTO.getToken();

    if (resetToken == null) {
      throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
    }

    String redisKey = "resetToken:" + resetToken;
    String uidStr = redisTemplate.opsForValue().get(redisKey);

    User user = userRepository.findByLoginId(uidStr)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    user.updatePassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
    userRepository.save(user);

    redisTemplate.delete(redisKey);
  }
}