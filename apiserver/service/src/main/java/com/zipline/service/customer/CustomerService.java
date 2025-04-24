package com.zipline.service.customer;

import java.util.List;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.counsel.dto.response.CounselListResponseDTO;
import com.zipline.service.customer.dto.request.CustomerModifyRequestDTO;
import com.zipline.service.customer.dto.request.CustomerRegisterRequestDTO;
import com.zipline.service.customer.dto.response.CustomerDetailResponseDTO;
import com.zipline.service.customer.dto.response.CustomerListResponseDTO;

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
