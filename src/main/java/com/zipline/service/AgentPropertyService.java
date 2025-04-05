package com.zipline.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.AgentPropertyRequestDTO;
import com.zipline.dto.AgentPropertyResponseDTO;
import com.zipline.entity.AgentProperty;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.agentProperty.PropertyNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.repository.AgentPropertyRepository;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentPropertyService {

	private final AgentPropertyRepository agentPropertyRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndIsDeletedFalse(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		return AgentPropertyResponseDTO.of(agentProperty);
	}

	@Transactional
	public AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid) {
		User loginedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = agentPropertyRequestDTO.toEntity(loginedUser, false, customer,
			LocalDateTime.now(),
			null, null);

		AgentProperty save = agentPropertyRepository.save(agentProperty);
		return AgentPropertyResponseDTO.of(save);
	}

	@Transactional
	public AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid) {
		User loginedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = agentPropertyRepository.findByUidAndIsDeletedFalse(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		agentProperty.modifyProperty(agentPropertyRequestDTO, loginedUser, false, customer,
			agentProperty.getCreatedAt(),
			LocalDateTime.now(), null);

		agentPropertyRepository.save(agentProperty);

		return AgentPropertyResponseDTO.of(agentProperty);
	}

	@Transactional
	public void deleteProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndIsDeletedFalse(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		System.out.println("agentProperty = " + agentProperty); // null인지 확인
		System.out.println("agentProperty.getUser() = " + agentProperty.getUser()); // 여기도

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		agentProperty.delete(LocalDateTime.now());
	}
}
