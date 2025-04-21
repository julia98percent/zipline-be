package com.zipline.service.user;

import com.zipline.dto.UserRequestDTO;
import com.zipline.dto.UserResponseDTO;
import com.zipline.global.jwt.dto.TokenRequestDTO;

public interface UserService {

	UserResponseDTO findById(Long uid);

	UserResponseDTO signup(UserRequestDTO userRequestDto);

	TokenRequestDTO login(UserRequestDTO userRequestDto);

	void logout(Long uid, String accessToken);

	UserResponseDTO updateInfo(Long uid, UserRequestDTO userRequestDto);

	TokenRequestDTO reissue(String refreshToken);
}
