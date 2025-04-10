package com.zipline.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.CustomerDetailResponseDTO;
import com.zipline.dto.CustomerListResponseDTO;
import com.zipline.dto.CustomerModifyRequestDTO;
import com.zipline.dto.CustomerRegisterRequestDTO;
import com.zipline.dto.PageRequestDTO;
import com.zipline.dto.agentProperty.AgentPropertyListResponseDTO;
import com.zipline.dto.counsel.CounselListResponseDTO;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CustomerController {

	private final CustomerService customerService;

	@GetMapping("/customers")
	public ResponseEntity<ApiResponse<CustomerListResponseDTO>> getCustomers(PageRequestDTO pageRequestDTO,
		Principal principal) {
		ApiResponse<CustomerListResponseDTO> response = customerService.getCustomers(pageRequestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/customers/{customerUid}")
	public ResponseEntity<ApiResponse<CustomerDetailResponseDTO>> getCustomer(
		@PathVariable("customerUid") Long customerUid, Principal principal) {
		CustomerDetailResponseDTO result = customerService.getCustomer(customerUid,
			Long.parseLong(principal.getName()));
		ApiResponse<CustomerDetailResponseDTO> response = ApiResponse.ok("회원 상세 정보 조회에 성공하였습니다.", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/customers/{customerUid}/properties")
	public ResponseEntity<ApiResponse<AgentPropertyListResponseDTO>> getCustomerProperties(
		@PathVariable("customerUid") Long customerUid,
		PageRequestDTO pageRequestDTO, Principal principal) {
		AgentPropertyListResponseDTO result = customerService.getCustomerProperties(customerUid,
			pageRequestDTO, Long.parseLong(principal.getName()));
		ApiResponse<AgentPropertyListResponseDTO> response = ApiResponse.ok("매물 내역 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/customers")
	public ResponseEntity<ApiResponse<Void>> registerCustomer(
		@Valid @RequestBody CustomerRegisterRequestDTO customerRegisterRequestDTO, Principal principal) {

		ApiResponse<Void> response = customerService.registerCustomer(customerRegisterRequestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/customers/{customerUid}/counsels")
	public ResponseEntity<ApiResponse<List<CounselListResponseDTO>>> getCustomerCounsels(@PathVariable Long customerUid,
		Principal principal) {
		List<CounselListResponseDTO> result = customerService.getCustomerCounsels(customerUid,
			Long.parseLong(principal.getName()));
		ApiResponse<List<CounselListResponseDTO>> response = ApiResponse.ok("상담 내역 조회 성공", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/customers/{customerUid}")
	public ResponseEntity<ApiResponse<CustomerDetailResponseDTO>> modifyCustomer(@PathVariable Long customerUid,
		@Valid @RequestBody CustomerModifyRequestDTO customerModifyRequestDTO, Principal principal) {

		ApiResponse<CustomerDetailResponseDTO> response = customerService.modifyCustomer(
			customerUid, customerModifyRequestDTO, Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/customers/{customerUid}")
	public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerUid, Principal principal) {
		ApiResponse<Void> response = customerService.deleteCustomer(customerUid,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
