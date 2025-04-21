package com.zipline.entity.contract;

import com.zipline.entity.customer.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerContract {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_uid", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_uid", nullable = false)
	private Contract contract;
}
