package com.zipline.entity.contract;

import java.time.LocalDate;

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
@Table(name = "contract_histories")
public class ContractHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_uid", nullable = false)
	private Contract contract;

	@Column(name = "prev_status", nullable = false, length = 20)
	private String prevStatus;

	@Column(name = "current_status", nullable = false, length = 20)
	private String currentStatus;

	@Column(name = "changed_at", nullable = false)
	private LocalDate changedAt;

	@Builder
	public ContractHistory(Contract contract, String prevStatus, String currentStatus, LocalDate changedAt) {
		this.contract = contract;
		this.prevStatus = prevStatus;
		this.currentStatus = currentStatus;
		this.changedAt = changedAt;
	}
}
