package com.zipline.service.contract;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractHistory;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.repository.contract.ContractHistoryRepository;
import com.zipline.service.contract.dto.response.ContractHistoryResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractHistoryServiceImpl implements ContractHistoryService {

	private final ContractHistoryRepository contractHistoryRepository;

	@Transactional
	public void addContractHistory(Contract contract, ContractStatus prevStatus, ContractStatus newStatus) {
		ContractHistory history = ContractHistory.builder()
			.contract(contract)
			.prevStatus(prevStatus.name())
			.currentStatus(newStatus.name())
			.changedAt(LocalDate.now())
			.build();

		contractHistoryRepository.save(history);
	}

	@Transactional(readOnly = true)
	public List<ContractHistoryResponseDTO> getHistoriesByContractUid(Long contractUid) {

		List<ContractHistory> histories = contractHistoryRepository.findByContractUidOrderByChangedAtDesc(contractUid);

		return histories.stream()
			.map(ContractHistoryResponseDTO::from)
			.toList();
	}
}
