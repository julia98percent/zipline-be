package com.zipline.service.customer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.label.Label;
import com.zipline.entity.label.LabelCustomer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.label.LabelException;
import com.zipline.global.exception.label.errorcode.LabelErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.contract.CustomerContractRepository;
import com.zipline.repository.counsel.CounselRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.label.LabelCustomerRepository;
import com.zipline.repository.label.LabelRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.counsel.dto.response.CounselListResponseDTO;
import com.zipline.service.customer.dto.request.CustomerModifyRequestDTO;
import com.zipline.service.customer.dto.request.CustomerRegisterRequestDTO;
import com.zipline.service.customer.dto.response.CustomerDetailResponseDTO;
import com.zipline.service.customer.dto.response.CustomerListResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final CounselRepository counselRepository;
	private final AgentPropertyRepository agentPropertyRepository;
	private final CustomerContractRepository customerContractRepository;
	private final LabelRepository labelRepository;
	private final LabelCustomerRepository labelCustomerRepository;

	@Transactional
	public void registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUid) {
		User loginedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Customer customer = customerRegisterRequestDTO.toEntity(loginedUser);
		customerRepository.save(customer);

		List<Long> requestLabelUids = customerRegisterRequestDTO.getLabelUids();
		if (requestLabelUids != null && !requestLabelUids.isEmpty()) {
			List<Label> validLabels = labelRepository.findAllByUidInAndUserUidAndDeletedAtIsNull(requestLabelUids,
				userUid);

			if (validLabels.size() != requestLabelUids.size()) {
				throw new LabelException(LabelErrorCode.LABEL_NOT_FOUND);
			}

			List<LabelCustomer> labelCustomers = validLabels.stream()
				.map(label -> new LabelCustomer(customer, label))
				.toList();

			labelCustomerRepository.saveAll(labelCustomers);
		}
	}

	@Transactional
	public CustomerDetailResponseDTO modifyCustomer(Long customerUid,
		CustomerModifyRequestDTO customerModifyRequestDTO, Long userUid) {

		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		List<Long> requestLabelUids = customerModifyRequestDTO.getLabelUids();

		if (requestLabelUids != null && !requestLabelUids.isEmpty()) {
			Set<Long> requestedLabelUids = new HashSet<>(requestLabelUids);

			List<Label> validLabels = labelRepository.findAllByUidInAndUserUidAndDeletedAtIsNull(requestLabelUids,
				userUid);
			if (validLabels.size() != requestedLabelUids.size()) {
				throw new LabelException(LabelErrorCode.LABEL_NOT_FOUND);
			}

			List<LabelCustomer> existingLabelMappings = labelCustomerRepository.findAllByCustomerUid(
				savedCustomer.getUid());
			Set<Long> existingLabelUids = existingLabelMappings.stream()
				.map(mapping -> mapping.getLabel().getUid())
				.collect(Collectors.toSet());

			List<LabelCustomer> toDeleteLabelMappings = existingLabelMappings.stream()
				.filter(mapping -> !requestedLabelUids.contains(mapping.getLabel().getUid()))
				.toList();
			labelCustomerRepository.deleteAll(toDeleteLabelMappings);

			Set<Long> labelUidsToAdd = new HashSet<>(requestedLabelUids);
			labelUidsToAdd.removeAll(existingLabelUids);

			if (!labelUidsToAdd.isEmpty()) {
				List<LabelCustomer> toAddLabelMappings = validLabels.stream()
					.filter(label -> labelUidsToAdd.contains(label.getUid()))
					.map(label -> new LabelCustomer(savedCustomer, label))
					.toList();
				labelCustomerRepository.saveAll(toAddLabelMappings);
			}
		}

		savedCustomer.modifyCustomer(customerModifyRequestDTO.getName(), customerModifyRequestDTO.getPhoneNo(),
			customerModifyRequestDTO.getTelProvider(),
			customerModifyRequestDTO.getLegalDistrictCode(),
			customerModifyRequestDTO.getMinRent(), customerModifyRequestDTO.getMaxRent(),
			customerModifyRequestDTO.getTrafficSource(),
			customerModifyRequestDTO.isTenant(), customerModifyRequestDTO.isLandlord(),
			customerModifyRequestDTO.isBuyer(),
			customerModifyRequestDTO.isSeller(), customerModifyRequestDTO.getMaxPrice(),
			customerModifyRequestDTO.getMinPrice(),
			customerModifyRequestDTO.getMinDeposit(), customerModifyRequestDTO.getMaxDeposit(),
			customerModifyRequestDTO.getBirthday());

		List<LabelCustomer> updatedLabelMappings = labelCustomerRepository.findAllByCustomerUid(
			savedCustomer.getUid());
		return new CustomerDetailResponseDTO(savedCustomer, updatedLabelMappings);
	}

	@Transactional
	public void deleteCustomer(Long customerUID, Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUID)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
		LocalDateTime deletedAt = LocalDateTime.now();
		savedCustomer.delete(deletedAt);
	}

	@Transactional(readOnly = true)
	public CustomerListResponseDTO getCustomers(PageRequestDTO pageRequestDTO, Long userUid) {
		Page<Customer> customerPage = customerRepository.findByUserUidAndDeletedAtIsNull(userUid,
			pageRequestDTO.toPageable());
		List<CustomerListResponseDTO.CustomerResponseDTO> customerResponseDTOList = customerPage.getContent().stream()
			.map(CustomerListResponseDTO.CustomerResponseDTO::new)
			.toList();

		return new CustomerListResponseDTO(customerResponseDTOList, customerPage);
	}

	@Transactional(readOnly = true)
	public CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndUserUidAndDeletedAtIsNull(customerUid, userUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		List<LabelCustomer> labelCustomerList = labelCustomerRepository.findAllByCustomerUid(customerUid);
		return new CustomerDetailResponseDTO(savedCustomer, labelCustomerList);
	}

	@Transactional(readOnly = true)
	public List<CounselListResponseDTO> getCustomerCounsels(Long customerUid, Long userUid) {
		validateCustomerExistence(customerUid, userUid);
		List<Counsel> savedCounsels = counselRepository.findByCustomerUidAndUserUidAndDeletedAtIsNullOrderByCreatedAtDesc(
			customerUid, userUid);
		return savedCounsels.stream().map(CounselListResponseDTO::createWithoutCustomerName).toList();
	}

	@Transactional(readOnly = true)
	public AgentPropertyListResponseDTO getCustomerProperties(Long customerUid, PageRequestDTO pageRequestDTO,
		Long userUid) {
		validateCustomerExistence(customerUid, userUid);
		Page<AgentProperty> savedAgentPropertiesPage = agentPropertyRepository.findByCustomerUidAndUserUidAndDeletedAtIsNullOrderByCreatedAtDesc(
			customerUid, userUid, pageRequestDTO.toPageable());

		List<AgentPropertyListResponseDTO.PropertyResponseDTO> agentPropertyData = savedAgentPropertiesPage.getContent()
			.stream()
			.map(AgentPropertyListResponseDTO.PropertyResponseDTO::new)
			.toList();
		return new AgentPropertyListResponseDTO(agentPropertyData, savedAgentPropertiesPage);
	}

	@Transactional(readOnly = true)
	public List<ContractListResponseDTO.ContractListDTO> getCustomerContracts(Long customerUid, Long userUid) {
		validateCustomerExistence(customerUid, userUid);
		List<CustomerContract> savedCustomerContract = customerContractRepository.findByCustomerUidAndUserUid(
			customerUid, userUid);
		return savedCustomerContract.stream()
			.map(ContractListResponseDTO.ContractListDTO::new)
			.toList();
	}

	private void validateCustomerExistence(Long customerUid, Long userUid) {
		if (!customerRepository.existsByUidAndUserUidAndDeletedAtIsNull(customerUid, userUid)) {
			throw new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND);
		}
	}
}
