package com.zipline.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.CustomerModifyRequestDTO;
import com.zipline.dto.CustomerModifyResponseDTO;
import com.zipline.dto.CustomerRegisterRequestDTO;
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

	@PostMapping("/customers")
	public ResponseEntity<ApiResponse<Void>> registerCustomer(
		@Valid @RequestBody CustomerRegisterRequestDTO customerRegisterRequestDTO, Principal principal) {

		ApiResponse<Void> response = customerService.registerCustomer(customerRegisterRequestDTO,
			Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/customers/{customerUid}")
	public ResponseEntity<ApiResponse<CustomerModifyResponseDTO>> modifyCustomer(@PathVariable Long customerUid,
		@Valid @RequestBody CustomerModifyRequestDTO customerModifyRequestDTO, Principal principal) {

		ApiResponse<CustomerModifyResponseDTO> response = customerService.modifyCustomer(
			customerUid, customerModifyRequestDTO, Long.parseLong(principal.getName()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
