package com.zipline.repository.contract;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.contract.ContractDocument;

public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long> {
}
