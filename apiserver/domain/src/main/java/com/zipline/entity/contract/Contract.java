package com.zipline.entity.contract;

import java.time.LocalDate;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.user.User;

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
@Table(name = "contracts")
public class Contract extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid", nullable = false)
	private User user;

	@Column(name = "category", length = 20)
	private String category;

	@Column(name = "contract_start_date")
	private LocalDate contractStartDate;

	@Column(name = "contract_date")
	private LocalDate contractDate;

	@Column(name = "contract_end_date")
	private LocalDate contractEndDate;

	@Column(name = "status", nullable = false)
	private ContractStatus status;

	@Column(name = "expected_contract_end_date")
	private LocalDate expectedContractEndDate;

	@Builder
	private Contract(User user, String category, LocalDate contractStartDate, LocalDate contractDate,
		LocalDate contractEndDate, ContractStatus status, LocalDate expectedContractEndDate) {
		this.user = user;
		this.category = category;
		this.contractStartDate = contractStartDate;
		this.contractDate = contractDate;
		this.contractEndDate = contractEndDate;
		this.status = status;
		this.expectedContractEndDate = expectedContractEndDate;
	}

	public void modifyContract(String category, LocalDate contractDate, LocalDate contractStartDate,
		LocalDate contractEndDate, LocalDate expectedContractEndDate, ContractStatus status) {
		this.category = category;
		this.contractDate = contractDate;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.expectedContractEndDate = expectedContractEndDate;
		this.status = status;
	}
}
