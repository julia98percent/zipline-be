package com.zipline.service.customer;

import java.util.List;

import com.zipline.dto.CustomerDetailResponseDTO;
import com.zipline.dto.CustomerListResponseDTO;
import com.zipline.dto.CustomerModifyRequestDTO;
import com.zipline.dto.CustomerRegisterRequestDTO;
import com.zipline.dto.PageRequestDTO;
import com.zipline.dto.agentProperty.AgentPropertyListResponseDTO;
import com.zipline.dto.contract.ContractListResponseDTO;
import com.zipline.dto.counsel.CounselListResponseDTO;
import com.zipline.global.response.ApiResponse;

public interface CustomerService {

	ApiResponse<Void> registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUID);

	ApiResponse<CustomerDetailResponseDTO> modifyCustomer(Long customerUid,
		CustomerModifyRequestDTO customerModifyRequestDTO,
		Long userUID);

	ApiResponse<Void> deleteCustomer(Long customerUID, Long userUID);

	ApiResponse<CustomerListResponseDTO> getCustomers(PageRequestDTO pageRequestDTO, Long userUID);

	CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid);

	List<CounselListResponseDTO> getCustomerCounsels(Long customerUid, Long userUid);

	AgentPropertyListResponseDTO getCustomerProperties(Long customerUid, PageRequestDTO pageRequestDTO, Long userUid);

	List<ContractListResponseDTO.ContractListDTO> getCustomerContracts(Long customerUid, Long userUid);
}
