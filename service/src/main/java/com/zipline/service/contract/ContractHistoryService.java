package com.zipline.service.contract;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.service.contract.dto.response.ContractHistoryResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.List;

public interface ContractHistoryService {

  @Timed
  void addContractHistory(Contract contract, ContractStatus prevStatus, ContractStatus newStatus);

  @Timed
  List<ContractHistoryResponseDTO> getHistoriesByContractUid(Long contractUid);
}