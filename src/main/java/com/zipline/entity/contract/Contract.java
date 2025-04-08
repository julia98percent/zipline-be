package com.zipline.entity.contract;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zipline.entity.enums.ContractStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@Column
	private String category;

	@Column
	private LocalDate contractStartDate;

	@Column
	private LocalDate contractDate;

	@Column
	private LocalDate contractEndDate;

	@Column(name = "status", nullable = false)
	private ContractStatus status;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

}
