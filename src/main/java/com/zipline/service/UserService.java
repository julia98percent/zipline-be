package com.zipline.service;

import java.time.Duration;
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

import com.zipline.dto.TokenRequestDto;
import com.zipline.dto.UserRequestDto;
import com.zipline.dto.UserResponseDto;
import com.zipline.entity.Authority;
import com.zipline.entity.User;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.jwt.TokenProvider;
import com.zipline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional(readOnly = true)
	public UserResponseDto findById(Long uid) {
		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다. id=" + uid, HttpStatus.BAD_REQUEST));

		return UserResponseDto.of(user);
	}

	@Transactional
	public UserResponseDto signup(UserRequestDto userRequestDto) {

		if (!userRequestDto.getPassword().equals(userRequestDto.getPasswordCheck())) {
			throw new UserNotFoundException("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsById(userRequestDto.getId())) {
			throw new UserNotFoundException("사용할 수 없는 아이디입니다.", HttpStatus.BAD_REQUEST);
		}

		User user = User.builder()
			.id(userRequestDto.getId())
			.password(passwordEncoder.encode(userRequestDto.getPassword()))
			.name(userRequestDto.getName())
			.role(Authority.ROLE_AGENT)
			.url(userRequestDto.getUrl())
			.birthday(userRequestDto.getBirthday())
			.phoneNo(userRequestDto.getPhoneNo())
			.email(userRequestDto.getEmail())
			.noticeMonth((userRequestDto.getNoticeMonth()))
			.build();

		userRepository.save(user);
		return UserResponseDto.of(user);
	}

	@Transactional
	public TokenRequestDto login(UserRequestDto userRequestDto) {

		// 0. 입력값 null 체크
		if (userRequestDto.getId().isBlank() || userRequestDto.getPassword().isBlank()) {
			throw new UserNotFoundException("아이디와 비밀번호를 모두 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		//1. 사용자 조회
		User user = userRepository.findByLoginId(userRequestDto.getId())
			.orElseThrow(() -> new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST));

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 권한 설정
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_AGENT"));

		// 4. 수동 인증 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUid(), null, authorities);

		TokenRequestDto tokenRequestDto = tokenProvider.generateTokenDto(authentication, user.getUid());

		redisTemplate.opsForValue().set(
			"refresh:" + user.getUid(),
			tokenRequestDto.getRefreshToken(),
			Duration.ofDays(7)
		);

		return tokenRequestDto;
	}

	public void logout(Long uid, String accessToken) {
		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		// 1. Access Token 검증
		if (!tokenProvider.validateToken(accessToken)) {
			throw new UserNotFoundException("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);
		}

		String refreshKey = "refresh:" + uid;
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

	public UserResponseDto updateInfo(Long uid, UserRequestDto userRequestDto) {
		User user = userRepository.findById(uid)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		user.updateInfo(userRequestDto);

		userRepository.save(user);
		return UserResponseDto.of(user);
	}

}