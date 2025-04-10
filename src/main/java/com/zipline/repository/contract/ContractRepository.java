package com.zipline.repository.contract;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.entity.contract.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
	Optional<Contract> findByUidAndIsDeletedFalse(Long contractUid);

	@Query("SELECT c FROM Contract c WHERE c.user.uid = :userUID AND c.isDeleted = :isDeleted ORDER BY c.uid DESC")
	Page<Contract> findByUserUidAndIsDeleted(Long userUID, boolean isDeleted, Pageable pageable);
}
