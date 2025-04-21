package com.zipline.repository.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.Contract;
import com.zipline.entity.contract.ContractDocument;

@Repository
public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long> {
	List<ContractDocument> findAllByContract(Contract contract);
}
