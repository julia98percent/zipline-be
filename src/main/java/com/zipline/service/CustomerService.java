package com.zipline.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.CustomerDetailResponseDTO;
import com.zipline.dto.CustomerListResponseDTO;
import com.zipline.dto.CustomerListResponseDTO.CustomerResponseDTO;
import com.zipline.dto.CustomerModifyRequestDTO;
import com.zipline.dto.CustomerRegisterRequestDTO;
import com.zipline.dto.PageRequestDTO;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;

	@Transactional
	public ApiResponse<Void> registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUID) {
		User loginedUser = userRepository.findById(userUID)
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		Customer customer = customerRegisterRequestDTO.toEntity(loginedUser, false, LocalDateTime.now(),
			null, null);
		customerRepository.save(customer);
		return ApiResponse.create("유저 등록에 성공하였습니다.");
	}

	@Transactional
	public ApiResponse<CustomerDetailResponseDTO> modifyCustomer(Long customerUid,
		CustomerModifyRequestDTO customerModifyRequestDTO, Long userUID) {

		Customer savedCustomer = customerRepository.findByUidAndIsDeletedFalse(customerUid)
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!savedCustomer.getUser().getUid().equals(userUID)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		savedCustomer.modifyCustomer(customerModifyRequestDTO.getName(), customerModifyRequestDTO.getPhoneNo(),
			customerModifyRequestDTO.getAddress(), customerModifyRequestDTO.getTelProvider(),
			customerModifyRequestDTO.getRegion(),
			customerModifyRequestDTO.getMinRent(), customerModifyRequestDTO.getMaxRent(),
			customerModifyRequestDTO.getTrafficSource(),
			customerModifyRequestDTO.isTenant(), customerModifyRequestDTO.isLandlord(),
			customerModifyRequestDTO.isBuyer(),
			customerModifyRequestDTO.isSeller(), customerModifyRequestDTO.getMaxPrice(),
			customerModifyRequestDTO.getMinPrice(),
			customerModifyRequestDTO.getMinDeposit(), customerModifyRequestDTO.getMaxDeposit(), LocalDateTime.now());

		CustomerDetailResponseDTO customerDetailResponseDTO = new CustomerDetailResponseDTO(savedCustomer);
		return ApiResponse.ok("고객 수정에 성공하였습니다.", customerDetailResponseDTO);
	}

	@Transactional
	public ApiResponse<Void> deleteCustomer(Long customerUID, Long userUID) {
		Customer savedCustomer = customerRepository.findByUidAndIsDeletedFalse(customerUID)
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!savedCustomer.getUser().getUid().equals(userUID)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		savedCustomer.delete(LocalDateTime.now());
		return ApiResponse.ok("고객 삭제에 성공하였습니다.");
	}

	@Transactional(readOnly = true)
	public ApiResponse<CustomerListResponseDTO> getCustomers(PageRequestDTO pageRequestDTO, Long userUID) {
		Page<Customer> customerPage = customerRepository.findByUserUidAndIsDeleted(userUID, false,
			pageRequestDTO.toPageable());
		List<CustomerResponseDTO> customerResponseDTOList = customerPage.getContent().stream()
			.map(CustomerResponseDTO::new)
			.toList();

		CustomerListResponseDTO result = new CustomerListResponseDTO(customerResponseDTOList, customerPage);

		return ApiResponse.ok("고객 목록 조회에 성공하였습니다.", result);
	}

	@Transactional(readOnly = true)
	public CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndIsDeletedFalse(customerUid)
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객 정보가 없습니다.", HttpStatus.BAD_REQUEST));

		if (!savedCustomer.getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		return new CustomerDetailResponseDTO(savedCustomer);
	}
}
