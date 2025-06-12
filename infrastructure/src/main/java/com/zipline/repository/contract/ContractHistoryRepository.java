package com.zipline.repository.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.ContractHistory;

@Repository
public interface ContractHistoryRepository extends JpaRepository<ContractHistory, Long> {
	List<ContractHistory> findByContractUidOrderByChangedAtDesc(Long contractUid);

	@Query("SELECT ch FROM ContractHistory ch WHERE ch.contract.uid IN :contractUids AND ch.deletedAt IS NULL ORDER BY ch.createdAt DESC")
	List<ContractHistory> findByContractUidsAndDeletedAtIsNull(List<Long> contractUids);
}
