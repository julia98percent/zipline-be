package com.zipline.service.agentProperty;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.agentProperty.AgentPropertyRequestDTO;
import com.zipline.dto.agentProperty.AgentPropertyResponseDTO;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.agentProperty.PropertyNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;
import com.zipline.repository.agentProperty.AgentPropertyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentPropertyService {

	private final AgentPropertyRepository agentPropertyRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findById(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		return AgentPropertyResponseDTO.of(agentProperty);
	}

	@Transactional
	public AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid) {
		User loggedInUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = agentPropertyRequestDTO.toEntity(loggedInUser, false, customer,
			LocalDateTime.now(),
			null, null);

		AgentProperty save = agentPropertyRepository.save(agentProperty);
		return AgentPropertyResponseDTO.of(save);
	}

	@Transactional
	public AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid) {
		User loggedInUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = agentPropertyRepository.findById(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		agentProperty.modifyProperty(agentPropertyRequestDTO, loggedInUser, false, customer,
			agentProperty.getCreatedAt(),
			LocalDateTime.now(), null);

		agentPropertyRepository.save(agentProperty);

		return AgentPropertyResponseDTO.of(agentProperty);

	}
}
