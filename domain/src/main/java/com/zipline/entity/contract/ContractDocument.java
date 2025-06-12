package com.zipline.entity.contract;

import com.zipline.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "contract_documents")
public class ContractDocument extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_uid", nullable = false)
	private Contract contract;

	@Column(name = "document_url")
	private String documentUrl;

	@Column(name = "document_name")
	private String documentName;

	@Builder
	private ContractDocument(Contract contract, String documentUrl, String documentName) {
		this.contract = contract;
		this.documentUrl = documentUrl;
		this.documentName = documentName;
	}
}

