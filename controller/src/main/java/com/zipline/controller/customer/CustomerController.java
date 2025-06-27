package com.zipline.controller.customer;


import com.zipline.global.request.CustomerFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.security.CustomUserDetails;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.counsel.CounselService;
import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.customer.CustomerService;
import com.zipline.service.customer.dto.request.CustomerModifyRequestDTO;
import com.zipline.service.customer.dto.request.CustomerRegisterRequestDTO;
import com.zipline.service.customer.dto.response.CustomerDetailResponseDTO;
import com.zipline.service.customer.dto.response.CustomerListResponseDTO;
import com.zipline.service.excel.ExcelService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class CustomerController {

  private final CustomerService customerService;
  private final CounselService counselService;
  private final ExcelService excelService;

  @GetMapping("/customers")
  public ResponseEntity<ApiResponse<CustomerListResponseDTO>> getCustomers(
      @ModelAttribute PageRequestDTO pageRequestDTO,
      @ModelAttribute CustomerFilterRequestDTO filterRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CustomerListResponseDTO result = customerService.getCustomers(pageRequestDTO, filterRequestDTO,
        userDetails.getUserUid());
    ApiResponse<CustomerListResponseDTO> response = ApiResponse.ok("고객 목록 조회에 성공하였습니다.", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/customers/{customerUid}")
  public ResponseEntity<ApiResponse<CustomerDetailResponseDTO>> getCustomer(
      @PathVariable("customerUid") Long customerUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CustomerDetailResponseDTO result = customerService.getCustomer(customerUid,
        userDetails.getUserUid());
    ApiResponse<CustomerDetailResponseDTO> response = ApiResponse.ok("회원 상세 정보 조회에 성공하였습니다.",
        result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/customers/{customerUid}/properties")
  public ResponseEntity<ApiResponse<AgentPropertyListResponseDTO>> getCustomerProperties(
      @PathVariable("customerUid") Long customerUid,
      PageRequestDTO pageRequestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
    AgentPropertyListResponseDTO result = customerService.getCustomerProperties(customerUid,
        pageRequestDTO, userDetails.getUserUid());
    ApiResponse<AgentPropertyListResponseDTO> response = ApiResponse.ok("매물 내역 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/customers")
  public ResponseEntity<ApiResponse<Void>> registerCustomer(
      @Valid @RequestBody CustomerRegisterRequestDTO customerRegisterRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    customerService.registerCustomer(customerRegisterRequestDTO, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.create("고객 등록에 성공하였습니다.");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/customers/{customerUid}/counsels")
  public ResponseEntity<ApiResponse<CounselPageResponseDTO>> getCustomerCounsels(
      @PathVariable Long customerUid,
      PageRequestDTO pageRequestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
    CounselPageResponseDTO result = customerService.getCustomerCounsels(customerUid, pageRequestDTO,
        userDetails.getUserUid());
    ApiResponse<CounselPageResponseDTO> response = ApiResponse.ok("상담 내역 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/customers/{customerUid}/contracts")
  public ResponseEntity<ApiResponse<ContractListResponseDTO>> getCustomerContracts(
      @PathVariable Long customerUid, PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ContractListResponseDTO result = customerService.getCustomerContracts(
        customerUid, pageRequestDTO, userDetails.getUserUid());
    ApiResponse<ContractListResponseDTO> response = ApiResponse.ok("계약 목록 조회 성공", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/customers/{customerUid}")
  public ResponseEntity<ApiResponse<CustomerDetailResponseDTO>> modifyCustomer(
      @PathVariable Long customerUid,
      @Valid @RequestBody CustomerModifyRequestDTO customerModifyRequestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    CustomerDetailResponseDTO result = customerService.modifyCustomer(
        customerUid, customerModifyRequestDTO, userDetails.getUserUid());
    ApiResponse<CustomerDetailResponseDTO> response = ApiResponse.ok("고객 수정에 성공하였습니다.", result);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/customers/{customerUid}")
  public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerUid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    customerService.deleteCustomer(customerUid, userDetails.getUserUid());
    ApiResponse<Void> response = ApiResponse.ok("고객 삭제에 성공하였습니다.");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/customers/{customerUid}/counsels")
  public ResponseEntity<ApiResponse<Map<String, Long>>> createCounsel(
      @PathVariable Long customerUid,
      @Valid @RequestBody CounselCreateRequestDTO requestDTO,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Map<String, Long> result = counselService.createCounsel(customerUid, requestDTO,
        userDetails.getUserUid());
    ApiResponse<Map<String, Long>> response = ApiResponse.create("상담 생성에 성공하였습니다.", result);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/customers/bulk")
  public ResponseEntity<ApiResponse<Map<String, Integer>>> registerCustomerByExcel(
      @RequestPart MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Map<String, Integer> result = excelService.registerCustomerByExcel(file,
        userDetails.getUserUid());
    ApiResponse<Map<String, Integer>> response = ApiResponse.create("고객 엑셀 등록 성공", result);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}