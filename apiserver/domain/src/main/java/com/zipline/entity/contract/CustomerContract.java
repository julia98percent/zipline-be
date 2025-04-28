package com.zipline.entity.contract;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.customer.Customer;

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
@Table(name = "customer_contracts")
public class CustomerContract extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_uid", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_uid", nullable = false)
	private Contract contract;

	@Builder
	private CustomerContract(Customer customer, Contract contract) {
		this.customer = customer;
		this.contract = contract;
	}

	public void updateCustomerContract(Customer customer) {
		this.customer = customer;
	}
}