package com.zipline.auth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zipline.auth.dto.AgentsRequestDto;
import com.zipline.auth.dto.TokenDto;
import com.zipline.auth.dto.UserResponseDto;
import com.zipline.auth.entity.Agents;
import com.zipline.auth.entity.Authority;
import com.zipline.auth.repository.AgentsRepository;
import com.zipline.global.exception.custom.AgentNotFoundException;
import com.zipline.global.jwt.TokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentsService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final AgentsRepository agentsRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Transactional
	public UserResponseDto signup(AgentsRequestDto agentsRequestDto) {
		if (agentsRepository.existsById(agentsRequestDto.getId())) {
			throw new RuntimeException("이미 가입되어있는 유저" );
		}

		Agents agents = Agents.builder()
			.agencyId(agentsRequestDto.getAgencyId())
			.id(agentsRequestDto.getId())
			.password(passwordEncoder.encode(agentsRequestDto.getPassword()))
			.name(agentsRequestDto.getName())
			.role(agentsRequestDto.getRole())
			.url(agentsRequestDto.getUrl())
			.birthday(agentsRequestDto.getBirthday())
			.qr(agentsRequestDto.getQr())
			.phoneNo(agentsRequestDto.getPhoneNo())
			.email(agentsRequestDto.getEmail())
			.certNo(agentsRequestDto.getCertNo())
			.certIssueDate(agentsRequestDto.getCertIssueDate())
			.authority(Authority.ROLE_USER)
			.build();

		agentsRepository.save(agents);
		return UserResponseDto.of(agents);  //**공통응답처리**//
	}

	@Transactional
	public TokenDto login(AgentsRequestDto agentsRequestDto) {

		UsernamePasswordAuthenticationToken authenticationToken = agentsRequestDto.toAuthentifation();

		// 1. 사용자 존재 확인
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		Agents agents = agentsRepository.findById(agentsRequestDto.getId())
			.orElseThrow(() -> new RuntimeException("가입되지 않은 사용자입니다." ));

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(agentsRequestDto.getPassword(), agents.getPassword())) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다." );
		}

		// 3. 인증 객체 생성
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER" ));

		// 4. Access Token 발급
		return tokenProvider.generateTokenDto(authentication, agents.getUid());
	}

	public void logout(Long uid) {
		Agents agents = agentsRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		agentsRepository.save(agents);
	}

	public UserResponseDto updateInfo(Long uid, AgentsRequestDto agentsRequestDto) {
		Agents agents = agentsRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		agents.updateInfo(agentsRequestDto);

		agentsRepository.save(agents);
		return UserResponseDto.of(agents);
	}

	public void updatePassword(String id, String currentPassword, String newPassword) {
		Agents agents = validatePassword(id, currentPassword);
		agents.updatePassword(passwordEncoder.encode((newPassword)));
	}

	public Agents validatePassword(String id, String currentPassword) {
		Agents agents = agentsRepository.findById(id)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		if (!passwordEncoder.matches(currentPassword, agents.getPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다." );
		}
		return agents;
	}

}

