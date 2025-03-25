package com.zipline.auth.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.auth.dto.AgentRequestDto;
import com.zipline.auth.dto.AgentResponseDto;
import com.zipline.auth.dto.AgentUpdatePasswordRequestDto;
import com.zipline.auth.dto.TokenDto;
import com.zipline.auth.entity.Agent;
import com.zipline.auth.repository.AgentRepository;
import com.zipline.auth.service.AgentService;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.AgentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/agents" )
@RequiredArgsConstructor
public class AgentController {

	private final AgentService agentService;
	private final AgentRepository agentRepository;

	@GetMapping("/me" )  // 특정 사용자 정보 조회
	public ResponseEntity<ApiResponse<Void>> findById(
		@RequestParam Long uid
	) {
		Agent agent = agentRepository.findById(uid)
			.orElseThrow(() -> new AgentNotFoundException("해당 유저를 찾을 수 없습니다. id=" + uid, HttpStatus.BAD_REQUEST));
		ApiResponse<Void> response = ApiResponse.ok("조회 성공" );
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/signup" )
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody AgentRequestDto agentRequestDto) {
		agentService.signup(agentRequestDto);
		ApiResponse<Void> response = ApiResponse.create("회원가입 성공" );
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login" )
	public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody AgentRequestDto agentRequestDto) {
		TokenDto tokenDto = agentService.login(agentRequestDto); // 로그인 & 토큰 발급

		ApiResponse<TokenDto> response = ApiResponse.ok("로그인 성공", tokenDto);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/logout" )
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal UserDetails userDetails,
		Principal principal
	) {
		Long uid = Long.parseLong(principal.getName());
		agentService.logout(uid);

		ApiResponse<Void> response = ApiResponse.ok("로그아웃 성공" );
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/update-info" )
	public ResponseEntity<ApiResponse<AgentResponseDto>> updateInfo(
		@RequestBody AgentRequestDto agentRequestDto,
		Principal principal
	) {
		Long uid = Long.parseLong(principal.getName());
		AgentResponseDto updatedInfo = agentService.updateInfo(uid, agentRequestDto);

		ApiResponse<AgentResponseDto> response = ApiResponse.ok("회원 정보 수정 완료", updatedInfo);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/update-password" )
	public ResponseEntity<ApiResponse<Void>> updatePassword(
		@AuthenticationPrincipal UserDetails userDetails,
		@Validated @RequestBody AgentUpdatePasswordRequestDto request
	) {
		log.info("로그인된 사용자 ID: {}", userDetails.getUsername());
		String id = userDetails.getUsername();

		agentService.updatePassword(id, request.getCurrentPassword(), request.getNewPassword());

		ApiResponse<Void> response = ApiResponse.ok("비밀번호 변경 완료" );
		return ResponseEntity.ok(response);
	}

}
