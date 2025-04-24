package com.zipline.service.user;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.zipline.global.jwt.TokenProvider;
import com.zipline.global.jwt.dto.TokenRequestDTO;
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

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

//todo: 추후 webconfig 활용 리펙터링 

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final SurveyRepository surveyRepository;
	private final PasswordQuestionRepository passwordQuestionRepository;
	private final PasswordQuestionAnswerRepository passwordQuestionAnswerRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final SurveyService surveyService;

	@Transactional(readOnly = true)
	public UserResponseDTO findById(Long uid) {
		User user = userRepository.findById(uid)
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

		PasswordQuestion question = passwordQuestionRepository.findById(signUpRequestDto.getPasswordQuestionUid())
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
	public TokenRequestDTO login(LoginRequestDTO loginRequestDTO) {
		User user = userRepository.findByLoginId(loginRequestDTO.getId())
			.orElseThrow(() -> new UserException(UserErrorCode.INVALID_CREDENTIALS));

		if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.INVALID_CREDENTIALS);
		}

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUid(), null, authorities);

		TokenRequestDTO tokenRequestDto = tokenProvider.generateTokenDto(authentication, user.getUid());

		redisTemplate.opsForValue().set(
			"refreshToken:" + user.getUid(),
			tokenRequestDto.getRefreshToken(),
			Duration.ofDays(7)
		);

		return tokenRequestDto;
	}

	public void logout(Long uid, String accessToken) {
		if (!tokenProvider.validateToken(accessToken)) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_CLIENT);
		}

		String refreshKey = "refreshToken:" + uid;
		redisTemplate.delete(refreshKey);

		Date expiration = tokenProvider.getExpiration(accessToken);
		long now = System.currentTimeMillis();
		long remainingExpiration = expiration.getTime() - now;

		if (remainingExpiration > 0) {
			redisTemplate.opsForValue().set(
				"blacklist:" + accessToken,
				"logout",
				Duration.ofMillis(remainingExpiration)
			);
		}
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

	@Transactional
	public TokenRequestDTO reissue(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new JwtException(AuthErrorCode.JWT_DECODE_FAIL.getMessage());
		}

		String uidStr = tokenProvider.getUserIdFromToken(refreshToken);
		Long uid = Long.parseLong(uidStr);
		String redisKey = "refreshToken:" + uidStr;
		String saveRefreshToken = redisTemplate.opsForValue().get(redisKey);

		if (saveRefreshToken == null || !saveRefreshToken.equals(refreshToken)) {
			throw new JwtException(AuthErrorCode.JWT_SIGNATURE_FAIL.getMessage());
		}

		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		List<GrantedAuthority> authorities =
			List.of(new SimpleGrantedAuthority(user.getRole().name()));

		Authentication authentication = new UsernamePasswordAuthenticationToken(uid, null, authorities);

		TokenRequestDTO tokenRequestDto = tokenProvider.generateTokenDto(authentication, uid);

		redisTemplate.opsForValue().set(redisKey, refreshToken, Duration.ofDays(7));

		return tokenRequestDto;
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

		PasswordQuestion question = passwordQuestionRepository.findById(findPasswordRequestDTO.getPasswordQuestionUid())
			.orElseThrow(() -> new UserException(UserErrorCode.PASSWORD_QUESTION_NOT_FOUND));

		PasswordQuestionAnswer answer = passwordQuestionAnswerRepository.findByUserAndPasswordQuestion(user, question)
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
			.set("resetToken:" + resetToken, user.getUid().toString(), Duration.ofMinutes(5));   //비밀번호 재설정 제한시간 5분

		return resetToken;
	}

	@Transactional
	public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
		if (!resetPasswordRequestDTO.getNewPassword().equals(resetPasswordRequestDTO.getNewPasswordCheck())) {
			throw new UserException(UserErrorCode.INVALID_PASSWORD_CHECK);
		}

		String redisKey = "resetToken:" + resetPasswordRequestDTO.getToken();
		String uidStr = redisTemplate.opsForValue().get(redisKey);

		if (uidStr == null) {
			throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
		}

		Long uid = Long.parseLong(uidStr);
		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		user.updatePassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
		userRepository.save(user);

		redisTemplate.delete(redisKey);
	}
}
