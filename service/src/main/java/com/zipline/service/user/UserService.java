package com.zipline.service.user;

import com.zipline.service.user.dto.request.FindPasswordRequestDTO;
import com.zipline.service.user.dto.request.FindUserIdRequestDTO;
import com.zipline.service.user.dto.request.LoginRequestDTO;
import com.zipline.service.user.dto.request.ResetPasswordRequestDTO;
import com.zipline.service.user.dto.request.SignUpRequestDTO;
import com.zipline.service.user.dto.request.UserModifyRequestDTO;
import com.zipline.service.user.dto.response.FindUserIdResponseDTO;
import com.zipline.service.user.dto.response.UserResponseDTO;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

  @Timed
  UserResponseDTO findById(Long uid);

  @Timed
  void signup(SignUpRequestDTO signUpRequestDto);

  @Timed
  UserResponseDTO authenticateAndLogin(LoginRequestDTO loginRequestDTO,
      HttpServletRequest request);

  @Timed
  UserResponseDTO updateInfo(Long uid, UserModifyRequestDTO userModifyRequestDto);

  @Timed
  FindUserIdResponseDTO findUserId(FindUserIdRequestDTO findUserIdRequestDto);

  @Timed
  String findUserPassword(FindPasswordRequestDTO findPasswordRequestDTO);

  @Timed
  void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}