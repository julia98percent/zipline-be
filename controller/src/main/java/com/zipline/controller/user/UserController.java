package com.zipline.controller.user;


import com.zipline.global.response.ApiResponse;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.user.UserService;
import com.zipline.service.user.dto.request.FindPasswordRequestDTO;
import com.zipline.service.user.dto.request.FindUserIdRequestDTO;
import com.zipline.service.user.dto.request.LoginRequestDTO;
import com.zipline.service.user.dto.request.ResetPasswordRequestDTO;
import com.zipline.service.user.dto.request.SignUpRequestDTO;
import com.zipline.service.user.dto.request.UserModifyRequestDTO;
import com.zipline.service.user.dto.response.FindUserIdResponseDTO;
import com.zipline.service.user.dto.response.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/info")  // 특정 사용자 정보 조회
  public ResponseEntity<ApiResponse<UserResponseDTO>> findById(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
      throw new AuthException(AuthErrorCode.UNAUTHORIZED_CLIENT);
    }
    UserResponseDTO dto = userService.findById(userDetails.getUserUid());
    ApiResponse<UserResponseDTO> response = ApiResponse.ok("조회 성공", dto);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<Void>> signup(
      @RequestBody @Valid SignUpRequestDTO signUpRequestDto) {
    userService.signup(signUpRequestDto);
    ApiResponse<Void> response = ApiResponse.create("회원가입 성공");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserResponseDTO>> login(
      @RequestBody @Valid LoginRequestDTO loginRequestDTO,
      HttpServletRequest request) {
    UserResponseDTO userInfo = userService.authenticateAndLogin(loginRequestDTO, request);
    ApiResponse<UserResponseDTO> responseBody = ApiResponse.ok("로그인 성공", userInfo);
    return ResponseEntity.ok(responseBody);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    request.getSession().invalidate();
    ApiResponse<Void> responseBody = ApiResponse.ok("로그아웃 성공");
    return ResponseEntity.ok(responseBody);
  }

  @PatchMapping("/info")
  public ResponseEntity<ApiResponse<UserResponseDTO>> updateInfo(
      @RequestBody UserModifyRequestDTO userModifyRequestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserResponseDTO updatedInfo = userService.updateInfo(userDetails.getUserUid(),
        userModifyRequestDto);

    ApiResponse<UserResponseDTO> response = ApiResponse.ok("회원 정보 수정 완료", updatedInfo);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/find-id")
  public ResponseEntity<ApiResponse<FindUserIdResponseDTO>> findUserId(
      @RequestBody @Valid FindUserIdRequestDTO findUserIdRequestDto) {
    FindUserIdResponseDTO findUserId = userService.findUserId(findUserIdRequestDto);
    ApiResponse<FindUserIdResponseDTO> response = ApiResponse.create("아이디 찾기 성공", findUserId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/find-password")
  public ResponseEntity<ApiResponse<String>> findUserPassword(
      @RequestBody @Valid FindPasswordRequestDTO findPasswordRequestDTO) {
    String resetToken = userService.findUserPassword(findPasswordRequestDTO);
    return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 토큰 발급", resetToken));
  }

  @PatchMapping("/reset-password")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
    userService.resetPassword(resetPasswordRequestDTO);
    return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 성공"));
  }

  @GetMapping("/csrf")
  public ResponseEntity<ApiResponse<String>> getCsrfToken(CsrfToken csrfToken) {
    return ResponseEntity.ok(ApiResponse.ok(csrfToken.getToken()));
  }
}