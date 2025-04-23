package com.zipline.service.user;

import com.zipline.dto.user.FindPasswordRequestDTO;
import com.zipline.dto.user.FindUserIdRequestDTO;
import com.zipline.dto.user.FindUserIdResponseDTO;
import com.zipline.dto.user.LoginRequestDTO;
import com.zipline.dto.user.ResetPasswordRequestDTO;
import com.zipline.dto.user.SignUpRequestDTO;
import com.zipline.dto.user.UserModifyRequestDTO;
import com.zipline.dto.user.UserResponseDTO;
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
