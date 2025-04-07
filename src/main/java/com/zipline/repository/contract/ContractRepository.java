package com.zipline.repository.contract;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.contract.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
