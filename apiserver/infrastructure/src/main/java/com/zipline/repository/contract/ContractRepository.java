package com.zipline.repository.contract;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
	Optional<Contract> findByUidAndDeletedAtIsNull(Long contractUid);

	@Query("SELECT c FROM Contract c WHERE c.user.uid = :userUID AND c.deletedAt IS NULL ORDER BY c.uid DESC")
	Page<Contract> findByUserUidAndDeletedAtIsNull(Long userUid, Pageable pageable);
}
