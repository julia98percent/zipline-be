package com.zipline.service.customer;

import com.zipline.global.request.CustomerFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.customer.dto.request.CustomerModifyRequestDTO;
import com.zipline.service.customer.dto.request.CustomerRegisterRequestDTO;
import com.zipline.service.customer.dto.response.CustomerDetailResponseDTO;
import com.zipline.service.customer.dto.response.CustomerListResponseDTO;
import io.micrometer.core.annotation.Timed;

public interface CustomerService {

  @Timed
  void registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUid);

  @Timed
  CustomerDetailResponseDTO modifyCustomer(Long customerUid,
      CustomerModifyRequestDTO customerModifyRequestDTO,
      Long userUid);

  @Timed
  void deleteCustomer(Long customerUID, Long userUid);

  @Timed
  CustomerListResponseDTO getCustomers(PageRequestDTO pageRequestDTO,
      CustomerFilterRequestDTO filterRequestDTO,
      Long userUid);

  @Timed
  CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid);

  @Timed
  CounselPageResponseDTO getCustomerCounsels(Long customerUid, PageRequestDTO pageRequestDTO,
      Long userUid);

  @Timed
  AgentPropertyListResponseDTO getCustomerProperties(Long customerUid,
      PageRequestDTO pageRequestDTO, Long userUid);

  @Timed
  ContractListResponseDTO getCustomerContracts(Long customerUid, PageRequestDTO pageRequestDTO,
      Long userUid);
}