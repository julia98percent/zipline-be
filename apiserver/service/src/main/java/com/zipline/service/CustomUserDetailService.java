package com.zipline.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zipline.entity.user.User;
import com.zipline.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return userRepository.findByLoginId(id)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
	}

	private UserDetails createUserDetails(User user) {

		return org.springframework.security.core.userdetails.User
			.withUsername(user.getId())
			.password(user.getPassword())
			.build();
	}
}
