package com.zipline.service.customer;

import com.zipline.global.request.CustomerFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.contract.dto.response.ContractListResponseDTO;
import com.zipline.service.counsel.dto.response.CounselListResponseDTO;
import com.zipline.service.customer.dto.request.CustomerModifyRequestDTO;
import com.zipline.service.customer.dto.request.CustomerRegisterRequestDTO;
import com.zipline.service.customer.dto.response.CustomerDetailResponseDTO;
import com.zipline.service.customer.dto.response.CustomerListResponseDTO;
import java.util.List;

public interface CustomerService {

	void registerCustomer(CustomerRegisterRequestDTO customerRegisterRequestDTO, Long userUid);

	CustomerDetailResponseDTO modifyCustomer(Long customerUid, CustomerModifyRequestDTO customerModifyRequestDTO,
		Long userUid);

	void deleteCustomer(Long customerUID, Long userUid);

	CustomerListResponseDTO getCustomers(PageRequestDTO pageRequestDTO, CustomerFilterRequestDTO filterRequestDTO,
		Long userUid);

	CustomerDetailResponseDTO getCustomer(Long customerUid, Long userUid);

	List<CounselListResponseDTO> getCustomerCounsels(Long customerUid, Long userUid);

	AgentPropertyListResponseDTO getCustomerProperties(Long customerUid, PageRequestDTO pageRequestDTO, Long userUid);


	ContractListResponseDTO getCustomerContracts(Long customerUid, PageRequestDTO pageRequestDTO, Long userUid);
}