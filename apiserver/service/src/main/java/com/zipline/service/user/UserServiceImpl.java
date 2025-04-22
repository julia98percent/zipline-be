package com.zipline.service.user;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.user.FindUserIdRequestDTO;
import com.zipline.dto.user.FindUserIdResponseDTO;
import com.zipline.dto.user.LoginRequestDTO;
import com.zipline.dto.user.SignUpRequestDTO;
import com.zipline.dto.user.UserModifyRequestDTO;
import com.zipline.dto.user.UserResponseDTO;
import com.zipline.entity.survey.Survey;
import com.zipline.entity.user.Authority;
import com.zipline.entity.user.User;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.jwt.ErrorCode;
import com.zipline.global.jwt.TokenProvider;
import com.zipline.global.jwt.dto.TokenRequestDTO;
import com.zipline.repository.UserRepository;
import com.zipline.repository.survey.SurveyRepository;
import com.zipline.service.survey.SurveyService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

//todo: 추후 webconfig 활용 리펙터링 

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final SurveyRepository surveyRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final SurveyService surveyService;

	@Transactional(readOnly = true)
	public UserResponseDTO findById(Long uid) {
		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다. id=" + uid, HttpStatus.BAD_REQUEST));
		Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> new RuntimeException("해당 유저의 설문이 존재하지 않습니다."));

		return UserResponseDTO.userSurvey(user, survey);
	}

	@Transactional
	public void signup(SignUpRequestDTO signUpRequestDto) {

		if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getPasswordCheck())) {
			throw new UserNotFoundException("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsById(signUpRequestDto.getId())) {
			throw new UserNotFoundException("사용할 수 없는 아이디입니다.", HttpStatus.BAD_REQUEST);
		}

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
	}

	@Transactional
	public TokenRequestDTO login(LoginRequestDTO loginRequestDTO) {
		// 0. 입력값 null 체크
		if (loginRequestDTO.getId().isBlank() || loginRequestDTO.getPassword().isBlank()) {
			throw new UserNotFoundException("아이디와 비밀번호를 모두 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		//1. 사용자 조회
		User user = userRepository.findByLoginId(loginRequestDTO.getId())
			.orElseThrow(() -> new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST));

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 권한 설정
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

		// 4. 수동 인증 객체 생성
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
		userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		// 1. Access Token 검증
		if (!tokenProvider.validateToken(accessToken)) {
			throw new UserNotFoundException("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);
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
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		Survey survey = surveyRepository.findFirstByUserOrderByCreatedAtDesc(user)
			.orElseThrow(() -> new RuntimeException("해당 유저의 설문이 존재하지 않습니다."));
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
			throw new JwtException(ErrorCode.JWT_DECODE_FAIL.getMessage());
		}

		String uidStr = tokenProvider.getUserIdFromToken(refreshToken);
		Long uid = Long.parseLong(uidStr);
		//refreshToken = "refreshToken: " + refreshToken;
		String redisKey = "refreshToken:" + uidStr;
		String saveRefreshToken = redisTemplate.opsForValue().get(redisKey);

		if (saveRefreshToken == null || !saveRefreshToken.equals(refreshToken)) {
			throw new JwtException(ErrorCode.JWT_SIGNATURE_FAIL.getMessage());
		}

		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

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
		).orElseThrow(() -> new UserNotFoundException("일치하는 사용자가 없습니다.", HttpStatus.BAD_REQUEST));

		return new FindUserIdResponseDTO(user.getId());
	}
}
