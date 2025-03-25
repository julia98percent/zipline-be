package com.zipline.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zipline.auth.entity.Agents;
import com.zipline.auth.repository.AgentsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomAgentsDetailService implements UserDetailsService {

	private final AgentsRepository agentsRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return agentsRepository.findById(id)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." ));
	}

	private UserDetails createUserDetails(Agents agents) {

		return org.springframework.security.core.userdetails.User
			.withUsername(agents.getId())
			.password(agents.getPassword())
			.build();
	}
}
