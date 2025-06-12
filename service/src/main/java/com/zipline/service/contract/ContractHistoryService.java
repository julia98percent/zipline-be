package com.zipline.service.contract;

import java.util.List;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.service.contract.dto.response.ContractHistoryResponseDTO;

public interface ContractHistoryService {

	void addContractHistory(Contract contract, ContractStatus prevStatus, ContractStatus newStatus);

	List<ContractHistoryResponseDTO> getHistoriesByContractUid(Long contractUid);
}
