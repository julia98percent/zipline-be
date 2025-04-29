package com.zipline.service.agentProperty;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.user.User;
import com.zipline.global.exception.agentProperty.PropertyException;
import com.zipline.global.exception.agentProperty.errorcode.PropertyErrorCode;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.AgentPropertyFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.agentProperty.AgentPropertyQueryRepository;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.contract.ContractRepository;
import com.zipline.repository.contract.CustomerContractRepository;
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
	private final ContractRepository contractRepository;
	private final CustomerContractRepository customerContractRepository;
	private final AgentPropertyQueryRepository agentPropertyQueryRepository;

	@Transactional(readOnly = true)
	public AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid) {
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndUserUidAndDeletedAtIsNull(propertyUid,
				userUid)
			.orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));

		return AgentPropertyResponseDTO.of(agentProperty);
	}

	@Transactional
	public AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid) {
		User loggedInUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Customer customer = customerRepository.findByUidAndUserUidAndDeletedAtIsNull(
				agentPropertyRequestDTO.getCustomerUid(), userUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		AgentProperty agentProperty = agentPropertyRequestDTO.toEntity(loggedInUser, customer);

		AgentProperty save = agentPropertyRepository.save(agentProperty);

		if (Boolean.TRUE.equals(agentPropertyRequestDTO.getCreateContract())) {
			Contract contract = Contract.builder()
				.user(loggedInUser)
				.status(ContractStatus.LISTED)
				.agentProperty(agentProperty)
				.build();
			CustomerContract customerContract = CustomerContract.builder()
				.customer(save.getCustomer())
				.contract(contract)
				.build();
			contractRepository.save(contract);
			customerContractRepository.save(customerContract);
		}
		return AgentPropertyResponseDTO.of(save);
	}

	@Transactional
	public AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid) {

		AgentProperty agentProperty = agentPropertyRepository.findByUidAndUserUidAndDeletedAtIsNull(propertyUid,
				userUid)
			.orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));

		Customer customer = customerRepository.findById(agentPropertyRequestDTO.getCustomerUid())
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

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
		AgentProperty agentProperty = agentPropertyRepository.findByUidAndUserUidAndDeletedAtIsNull(propertyUid,
				userUid)
			.orElseThrow(() -> new PropertyException(PropertyErrorCode.PROPERTY_NOT_FOUND));

		agentProperty.delete(LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public AgentPropertyListResponseDTO getAgentPropertyList(PageRequestDTO pageRequestDTO, Long userUid,
		AgentPropertyFilterRequestDTO detailFilter) {
		Page<AgentProperty> agentPropertyPage = agentPropertyQueryRepository.findFilteredProperties(
			userUid, detailFilter, pageRequestDTO.toPageable()
		);
		List<PropertyResponseDTO> agentPropertyResponseDTOList = agentPropertyPage.getContent().stream()
			.map(PropertyResponseDTO::new)
			.toList();

		return new AgentPropertyListResponseDTO(agentPropertyResponseDTOList, agentPropertyPage);
	}
}
