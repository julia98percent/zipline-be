package com.zipline.repository.contract;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;

public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {
	@Query("SELECT cc FROM CustomerContract cc Where cc.contract.uid IN :contractIds")
	List<CustomerContract> findInContractUids(List<Long> contractIds);

	Optional<CustomerContract> findByContract(Contract contract);
}
