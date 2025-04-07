package com.zipline.repository.contract;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.contract.CustomerContract;

public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {
}
