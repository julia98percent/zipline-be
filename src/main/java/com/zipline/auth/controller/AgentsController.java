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

import com.zipline.auth.dto.AgentsRequestDto;
import com.zipline.auth.dto.AgentsUpdatePasswordRequest;
import com.zipline.auth.dto.UserResponseDto;
import com.zipline.auth.entity.Agents;
import com.zipline.auth.repository.AgentsRepository;
import com.zipline.auth.service.AgentsService;
import com.zipline.global.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/agents" )
@RequiredArgsConstructor
public class AgentsController {

	private final AgentsService agentsService;
	private final AgentsRepository agentsRepository;

	@GetMapping("/me" )  // 특정 사용자 정보 조회
	public ResponseEntity<UserResponseDto> findById(
		@RequestParam Long uid
	) {
		Agents agents = agentsRepository.findById(uid)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + uid));

		return ResponseEntity.ok(UserResponseDto.of(agents));
	}

	@PostMapping("/signup" )
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody AgentsRequestDto agentsRequestDto) {
		agentsService.signup(agentsRequestDto);
		ApiResponse<Void> response = ApiResponse.create("회원가입 성공" );
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login" )
	public ResponseEntity<ApiResponse<Void>> login(
		@RequestBody AgentsRequestDto agentsRequestDto
	) {
		ApiResponse<Void> response = ApiResponse.create("로그인 성공" );
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("/logout" )
	public ResponseEntity<String> logout(
		@AuthenticationPrincipal UserDetails userDetails
		, Principal principal
	) {
		Long uid = Long.parseLong(principal.getName());
		agentsService.logout(uid);
		return ResponseEntity.ok("로그아웃 성공" );
	}

	@PatchMapping("/updateinfo" )
	public ResponseEntity<UserResponseDto> updateInfo(@RequestBody AgentsRequestDto agentsRequestDto,
		Principal principal) {
		Long uid = Long.parseLong(principal.getName());
		agentsService.updateInfo(uid, agentsRequestDto);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@PatchMapping("/updatepassword" )
	public void updatePassword(@AuthenticationPrincipal UserDetails userDetails,
		@Validated @RequestBody AgentsUpdatePasswordRequest agentsUpdatePasswordRequest) {
		log.info("로그인된 사용자 ID: {}", userDetails.getUsername());
		String id = userDetails.getUsername();
		agentsService.updatePassword(id, agentsUpdatePasswordRequest.getCurrentPassword(),
			agentsUpdatePasswordRequest.getNewPassword());
	}

}
