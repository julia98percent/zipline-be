package com.zipline.controller.contract;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.contract.ContractRequestDTO;
import com.zipline.dto.contract.ContractResponseDTO;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.contract.ContractService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contracts")
public class ContractController {

	private final ContractService contractService;

	@GetMapping("/{contractUid}")
	public ResponseEntity<ApiResponse<ContractResponseDTO>> getContract(@PathVariable Long contractUid) {
		ContractResponseDTO contractResponseDTO = contractService.getContract(contractUid);
		ApiResponse<ContractResponseDTO> response = ApiResponse.ok("계약 상세 조회 성공", contractResponseDTO);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("")
	public ResponseEntity<ApiResponse<Void>> registerContract(@RequestBody ContractRequestDTO contractRequestDTO,
		Principal principal) {
		contractService.registerContract(contractRequestDTO, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.create("계약 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
