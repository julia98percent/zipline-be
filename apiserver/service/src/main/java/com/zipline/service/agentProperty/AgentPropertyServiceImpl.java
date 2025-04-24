package com.zipline.service.agentProperty;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.agentProperty.PropertyNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.global.exception.custom.user.UserNotFoundException;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.agentProperty.dto.request.AgentPropertyRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO.PropertyResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentPropertyServiceImpl implements AgentPropertyService {

	private final AgentPropertyRepository agentPropertyRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;

	@Transactional(readOnly = true)
	public AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndDeletedAtIsNull(propertyUid)
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

		AgentProperty agentProperty = agentPropertyRequestDTO.toEntity(loggedInUser, customer);

		AgentProperty save = agentPropertyRepository.save(agentProperty);
		return AgentPropertyResponseDTO.of(save);
	}

	@Transactional
	public AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid) {
		User loggedInUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		AgentProperty agentProperty = agentPropertyRepository.findByUidAndDeletedAtIsNull(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		agentProperty.modifyProperty(customer, agentPropertyRequestDTO.getAddress(),
			agentPropertyRequestDTO.getLegalDistrictCode(), agentPropertyRequestDTO.getDeposit(),
			agentPropertyRequestDTO.getMonthlyRent(), agentPropertyRequestDTO.getPrice(),
			agentPropertyRequestDTO.getType(), agentPropertyRequestDTO.getLongitude(),
			agentPropertyRequestDTO.getLatitude(), agentPropertyRequestDTO.getStartDate(),
			agentPropertyRequestDTO.getEndDate(), agentPropertyRequestDTO.getMoveInDate(),
			agentPropertyRequestDTO.getRealCategory(), agentPropertyRequestDTO.getPetsAllowed(),
			agentPropertyRequestDTO.getFloor(), agentPropertyRequestDTO.getHasElevator(),
			agentPropertyRequestDTO.getConstructionYear(), agentPropertyRequestDTO.getParkingCapacity(),
			agentPropertyRequestDTO.getNetArea(), agentPropertyRequestDTO.getTotalArea(),
			agentPropertyRequestDTO.getDetails());

		agentPropertyRepository.save(agentProperty);

		return AgentPropertyResponseDTO.of(agentProperty);
	}

	@Transactional
	public void deleteProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndDeletedAtIsNull(propertyUid)
			.orElseThrow(() -> new PropertyNotFoundException("해당 매물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!agentProperty.getUser().getUid().equals(userUid))
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);

		agentProperty.delete(LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public AgentPropertyListResponseDTO getAgentPropertyList(PageRequestDTO pageRequestDTO, Long userUid) {
		Page<AgentProperty> agentPropertyPage = agentPropertyRepository.findByUserUidAndDeletedAtIsNull(userUid,
			pageRequestDTO.toPageable());
		List<PropertyResponseDTO> agentPropertyResponseDTOList = agentPropertyPage.getContent().stream()
			.map(PropertyResponseDTO::new)
			.toList();

		return new AgentPropertyListResponseDTO(agentPropertyResponseDTOList, agentPropertyPage);
	}
}
