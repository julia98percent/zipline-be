package com.zipline.controller;

import java.security.Principal;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.TokenRequestDto;
import com.zipline.dto.TokenResponseDto;
import com.zipline.dto.UserRequestDto;
import com.zipline.dto.UserResponseDto;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/me")  // 특정 사용자 정보 조회
	public ResponseEntity<ApiResponse<UserResponseDto>> findById(Principal principal) {
		Long uid = Long.parseLong(principal.getName());
		UserResponseDto dto = userService.findById(uid);
		ApiResponse<UserResponseDto> response = ApiResponse.ok("조회 성공", dto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody UserRequestDto userRequestDto) {
		userService.signup(userRequestDto);
		ApiResponse<Void> response = ApiResponse.create("회원가입 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponseDto>> login(
		@RequestBody UserRequestDto userRequestDto,
		HttpServletResponse response) {

		TokenRequestDto tokenRequestDto = userService.login(userRequestDto); // 로그인 & 토큰 발급

		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenRequestDto.getRefreshToken())
			.httpOnly(true)
			.secure(true)  //https에서만 전송하려면 true로 전환
			.path("/")
			.maxAge(Duration.ofDays(7))
			.sameSite("None")
			.build();

		response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
			.uid(tokenRequestDto.getUid())
			.grantType(tokenRequestDto.getGrantType())
			.accessToken(tokenRequestDto.getAccessToken())
			.build();

		ApiResponse<TokenResponseDto> responseBody = ApiResponse.ok("로그인 성공", tokenResponseDto);
		return ResponseEntity.ok(responseBody);
	}

	@PatchMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestHeader("Authorization") String authorizationHeader,
		HttpServletResponse response
	) {
		Long uid = Long.parseLong(userDetails.getUsername());
		String accessToken = authorizationHeader.replace("Bearer ", "");
		userService.logout(uid, accessToken);

		ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
			.httpOnly(true)
			.secure(false)  //https에서만 전송하려면 true로 전환
			.path("/")
			.maxAge(0) // 즉시 만료
			.sameSite("Strict")
			.build();
		response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

		ApiResponse<Void> responseBody = ApiResponse.ok("로그아웃 성공");
		return ResponseEntity.ok(responseBody);
	}

	@PatchMapping("/update-info")
	public ResponseEntity<ApiResponse<UserResponseDto>> updateInfo(
		@RequestBody UserRequestDto userRequestDto,
		Principal principal) {
		Long uid = Long.parseLong(principal.getName());
		UserResponseDto updatedInfo = userService.updateInfo(uid, userRequestDto);

		ApiResponse<UserResponseDto> response = ApiResponse.ok("회원 정보 수정 완료", updatedInfo);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/reissue")
	public ResponseEntity<ApiResponse<TokenResponseDto>> reissue(@CookieValue("refreshToken") String refreshToken) {
		TokenRequestDto tokenRequestDto = userService.reissue(refreshToken);

		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenRequestDto.getRefreshToken())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofDays(7))
			.sameSite("None")
			.build();

		TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
			.uid(tokenRequestDto.getUid())
			.grantType(tokenRequestDto.getGrantType())
			.accessToken(tokenRequestDto.getAccessToken())
			.build();

		ApiResponse<TokenResponseDto> response = ApiResponse.ok("AccessToken 재발급 성공", tokenResponseDto);
		return ResponseEntity.ok(response);

	}

}
