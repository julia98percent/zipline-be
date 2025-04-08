package com.zipline.repository.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;

public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long> {
	List<ContractDocument> findAllByContract(Contract contract);
}
