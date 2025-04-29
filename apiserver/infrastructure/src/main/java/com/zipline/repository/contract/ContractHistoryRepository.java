package com.zipline.repository.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.ContractHistory;

@Repository
public interface ContractHistoryRepository extends JpaRepository<ContractHistory, Long> {
	List<ContractHistory> findByContractUidOrderByChangedAtDesc(Long contractUid);
}
