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
import com.zipline.global.exception.custom.UserNotFoundException;
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

	@Transactional
	public AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid) {
		User loginedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = AgentProperty.builder()
			.user(loginedUser)
			.customer(customer)
			.address(agentPropertyRequestDTO.getAddress())
			/*.address1(agentPropertyRequestDTO.getDong())
			.address2(agentPropertyRequestDTO.getRoadName())*/
			.address3(agentPropertyRequestDTO.getExtraAddress()) // 상세 주소
			.deposit(agentPropertyRequestDTO.getDeposit())
			.monthlyRent(agentPropertyRequestDTO.getMonthlyRent())
			.price(agentPropertyRequestDTO.getPrice())
			.type(agentPropertyRequestDTO.getType())
			.longitude(agentPropertyRequestDTO.getLongitude())
			.latitude(agentPropertyRequestDTO.getLatitude())
			.startDate(agentPropertyRequestDTO.getStartDate())
			.endDate(agentPropertyRequestDTO.getEndDate())
			.moveInDate(agentPropertyRequestDTO.getMoveInDate())
			.realCategory(agentPropertyRequestDTO.getRealCategory())
			.petsAllowed(agentPropertyRequestDTO.getPetsAllowed())
			.floor(agentPropertyRequestDTO.getFloor())
			.hasElevator(agentPropertyRequestDTO.getHasElevator())
			.constructionYear(agentPropertyRequestDTO.getConstructionYear())
			.parkingCapacity(agentPropertyRequestDTO.getParkingCapacity())
			.netArea(agentPropertyRequestDTO.getNetArea())
			.totalArea(agentPropertyRequestDTO.getTotalArea())
			.details(agentPropertyRequestDTO.getDetails())
			.isDeleted(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.deletedAt(null)
			.build();

		AgentProperty save = agentPropertyRepository.save(agentProperty);
		return AgentPropertyResponseDTO.of(save);
	}
}
