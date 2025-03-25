package com.zipline.auth.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zipline.auth.dto.AgentRequestDto;
import com.zipline.auth.dto.AgentResponseDto;
import com.zipline.auth.dto.TokenDto;
import com.zipline.auth.entity.Agent;
import com.zipline.auth.entity.Authority;
import com.zipline.auth.repository.AgentRepository;
import com.zipline.global.exception.custom.AgentNotFoundException;
import com.zipline.global.jwt.TokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService {

	private final AgentRepository agentRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Transactional
	public AgentResponseDto findById(Long uid) {
		Agent agent = agentRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("해당 유저를 찾을 수 없습니다. id=" + uid, HttpStatus.BAD_REQUEST));

		return AgentResponseDto.of(agent);
	}

	@Transactional
	public AgentResponseDto signup(AgentRequestDto agentRequestDto) {

		if (!agentRequestDto.getPassword().equals(agentRequestDto.getPasswordCheck())) {
			throw new AgentNotFoundException("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		if (agentRepository.existsById(agentRequestDto.getId())) {
			throw new AgentNotFoundException("이미 가입되어있는 유저입니다.", HttpStatus.BAD_REQUEST);
		}

		Agent agent = Agent.builder()
			.id(agentRequestDto.getId())
			.password(passwordEncoder.encode(agentRequestDto.getPassword()))
			.name(agentRequestDto.getName())
			.role(agentRequestDto.getRole())
			.url(agentRequestDto.getUrl())
			.birthday(agentRequestDto.getBirthday())
			.qr(agentRequestDto.getQr())
			.phoneNo(agentRequestDto.getPhoneNo())
			.email(agentRequestDto.getEmail())
			.certNo(agentRequestDto.getCertNo())
			.certIssueDate(agentRequestDto.getCertIssueDate())
			.authority(Authority.ROLE_USER)
			.build();

		agentRepository.save(agent);
		return AgentResponseDto.of(agent);
	}

	@Transactional
	public TokenDto login(AgentRequestDto agentRequestDto) {

		Agent agent = agentRepository.findById(agentRequestDto.getId())
			.orElseThrow(() -> new AgentNotFoundException("가입되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST));

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(agentRequestDto.getPassword(), agent.getPassword())) {
			throw new AgentNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 3. 권한 설정
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER" ));

		// 4. 수동 인증 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			agent.getUid(), null, authorities
		);

		return tokenProvider.generateTokenDto(authentication, agent.getUid());
	}

	public void logout(Long uid) {
		Agent agent = agentRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		agentRepository.save(agent);
	}

	public AgentResponseDto updateInfo(Long uid, AgentRequestDto agentRequestDto) {
		Agent agent = agentRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		agent.updateInfo(agentRequestDto);

		agentRepository.save(agent);
		return AgentResponseDto.of(agent);
	}

	public void updatePassword(String id, String currentPassword, String newPassword) {
		Agent agent = validatePassword(id, currentPassword);
		agent.updatePassword(passwordEncoder.encode((newPassword)));
	}

	public Agent validatePassword(String id, String currentPassword) {
		Agent agent = agentRepository.findById(id)
			.orElseThrow(() -> new AgentNotFoundException("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		if (!passwordEncoder.matches(currentPassword, agent.getPassword())) {
			throw new AgentNotFoundException("현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return agent;
	}

}

