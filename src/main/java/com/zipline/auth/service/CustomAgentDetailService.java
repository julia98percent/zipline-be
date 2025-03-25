package com.zipline.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zipline.auth.entity.Agent;
import com.zipline.auth.repository.AgentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomAgentDetailService implements UserDetailsService {

	private final AgentRepository agentRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return agentRepository.findById(id)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." ));
	}

	private UserDetails createUserDetails(Agent agent) {

		return org.springframework.security.core.userdetails.User
			.withUsername(agent.getId())
			.password(agent.getPassword())
			.build();
	}
}
