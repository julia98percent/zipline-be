package com.zipline.security;

import com.zipline.entity.user.User;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UserException {
    User user = userRepository.findByLoginId(username)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    return new CustomUserDetails(user);
  }
}