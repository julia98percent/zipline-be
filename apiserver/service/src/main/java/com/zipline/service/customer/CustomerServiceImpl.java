package com.zipline.service.customer;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.contract.CustomerContract;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.repository.agentProperty.AgentPropertyRepository;
import com.zipline.repository.contract.CustomerContractRepository;
import com.zipline.repository.counsel.CounselRepository;
import com.zipline.repository.customer.CustomerRepository;
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

	@Transactional
	public ApiResponse<Void> registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUID) {
		User loginedUser = userRepository.findById(userUID)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Customer customer = customerRegisterRequestDTO.toEntity(loginedUser);
		customerRepository.save(customer);
		return ApiResponse.create("유저 등록에 성공하였습니다.");
	}

	@Transactional
	public ApiResponse<CustomerDetailResponseDTO> modifyCustomer(Long customerUid,
		CustomerModifyRequestDTO customerModifyRequestDTO, Long userUID) {

		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		if (!savedCustomer.getUser().getUid().equals(userUID)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
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

		CustomerDetailResponseDTO customerDetailResponseDTO = new CustomerDetailResponseDTO(savedCustomer);
		return ApiResponse.ok("고객 수정에 성공하였습니다.", customerDetailResponseDTO);
	}

	@Transactional
	public ApiResponse<Void> deleteCustomer(Long customerUID, Long userUID) {
		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUID)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		if (!savedCustomer.getUser().getUid().equals(userUID)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
		}

		savedCustomer.delete(LocalDateTime.now());
		return ApiResponse.ok("고객 삭제에 성공하였습니다.");
	}

	@Transactional(readOnly = true)
	public ApiResponse<CustomerListResponseDTO> getCustomers(PageRequestDTO pageRequestDTO, Long userUID) {
		Page<Customer> customerPage = customerRepository.findByUserUidAndDeletedAtIsNull(userUID,
			pageRequestDTO.toPageable());
		List<CustomerListResponseDTO.CustomerResponseDTO> customerResponseDTOList = customerPage.getContent().stream()
			.map(CustomerListResponseDTO.CustomerResponseDTO::new)
			.toList();

		CustomerListResponseDTO result = new CustomerListResponseDTO(customerResponseDTOList, customerPage);

		return ApiResponse.ok("고객 목록 조회에 성공하였습니다.", result);
	}

	@Transactional(readOnly = true)
	public CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));

		if (!savedCustomer.getUser().getUid().equals(userUid)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
		}

		return new CustomerDetailResponseDTO(savedCustomer);
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
		List<ContractListResponseDTO.ContractListDTO> result = savedCustomerContract.stream()
			.map(cc -> new ContractListResponseDTO.ContractListDTO(cc))
			.toList();
		return result;
	}

	private void validateCustomerExistence(Long customerUid, Long userUid) {
		if (!customerRepository.existsByUidAndUserUidAndDeletedAtIsNull(customerUid, userUid)) {
			throw new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND);
		}
	}
}
