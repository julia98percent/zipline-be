package com.zipline.repository.contract;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.enums.ContractStatus;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
	Optional<Contract> findByUidAndUserUidAndDeletedAtIsNull(Long contractUid, Long userUid);

	int countByUserUidAndCreatedAtAfter(Long userId, LocalDateTime oneMonthAgo);

	int countByUserUidAndStatusIn(Long userId, List<ContractStatus> statuses);

	@Query("SELECT c FROM Contract c WHERE c.user.uid = :userUid AND c.agentProperty.uid = :propertyUid AND c.deletedAt IS NULL AND c.status IN :includedStatuses ORDER BY c.createdAt DESC LIMIT 1")
	Contract findByUserUidAndAgentPropertyUidAndAndContractStatusNotCanceledAndDeletedAtIsNull(Long userUid,
		Long propertyUid, List<ContractStatus> includedStatuses);

	@Query("SELECT c FROM Contract c WHERE c.user.uid = :userUid AND c.agentProperty.uid = :propertyUid AND c.deletedAt IS NULL AND c.status IN :closedStatuses ORDER BY c.createdAt DESC")
	List<Contract> findByUserUidAndAgentPropertyUidAndContractStatusCanceledDeletedAtIsNull(Long userUid,
		Long propertyUid, List<ContractStatus> closedStatuses);
}
