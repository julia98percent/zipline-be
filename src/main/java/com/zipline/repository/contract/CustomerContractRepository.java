package com.zipline.repository.contract;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.CustomerContract;

public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {
	Optional<CustomerContract> findByContract(Contract contract);
}
