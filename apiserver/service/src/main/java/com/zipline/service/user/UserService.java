package com.zipline.service.user;

import com.zipline.service.user.dto.request.FindUserIdRequestDTO;
import com.zipline.service.user.dto.request.FindPasswordRequestDTO;
import com.zipline.service.user.dto.request.ResetPasswordRequestDTO;
import com.zipline.service.user.dto.response.FindUserIdResponseDTO;
import com.zipline.service.user.dto.request.LoginRequestDTO;
import com.zipline.service.user.dto.request.SignUpRequestDTO;
import com.zipline.service.user.dto.request.UserModifyRequestDTO;
import com.zipline.service.user.dto.response.UserResponseDTO;
import com.zipline.global.jwt.dto.TokenRequestDTO;

public interface UserService {

	UserResponseDTO findById(Long uid);

	void signup(SignUpRequestDTO signUpRequestDto);

	TokenRequestDTO login(LoginRequestDTO loginRequestDTO);

	void logout(Long uid, String accessToken);

	UserResponseDTO updateInfo(Long uid, UserModifyRequestDTO userModifyRequestDto);

	TokenRequestDTO reissue(String refreshToken);

	FindUserIdResponseDTO findUserId(FindUserIdRequestDTO findUserIdRequestDto);

	String findUserPassword(FindPasswordRequestDTO findPasswordRequestDTO);

	void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}
