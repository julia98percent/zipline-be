package com.zipline.controller.contract;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.contract.ContractService;
import com.zipline.service.contract.dto.request.ContractRequestDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.contract.dto.response.ContractResponseDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "계약", description = "계약 관련 API")
@RequestMapping("/api/v1/contracts")
public class ContractController {

	private final ContractService contractService;

	@GetMapping("/{contractUid}")
	public ResponseEntity<ApiResponse<ContractResponseDTO>> getContract(@PathVariable Long contractUid,
		Principal principal) {
		ContractResponseDTO contractResponseDTO = contractService.getContract(contractUid, Long.parseLong(
			principal.getName()));
		ApiResponse<ContractResponseDTO> response = ApiResponse.ok("계약 상세 조회 성공", contractResponseDTO);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<Void>> registerContract(@RequestPart ContractRequestDTO contractRequestDTO,
		@RequestPart List<MultipartFile> files,
		Principal principal) {
		contractService.registerContract(contractRequestDTO, files, Long.parseLong(principal.getName()));
		ApiResponse<Void> response = ApiResponse.create("계약 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("")
	public ResponseEntity<ApiResponse<ContractListResponseDTO>> getContractList(PageRequestDTO pageRequestDTO,
		Principal principal) {
		Long userUid = Long.parseLong(principal.getName());

		ContractListResponseDTO responseDto = contractService.getContractList(pageRequestDTO, userUid);
		ApiResponse<ContractListResponseDTO> response = ApiResponse.ok("계약 목록 조회 성공", responseDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
