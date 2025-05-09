package com.zipline.entity.contract;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.enums.ContractStatus;
import com.zipline.entity.enums.PropertyType;
import com.zipline.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
	@Enumerated(EnumType.STRING)
	private PropertyType category;

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

	@Column(name = "deposit")
	private BigInteger deposit;

	@Column(name = "monthly_rent")
	private BigInteger monthlyRent;

	@Column(name = "price")
	private BigInteger price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agent_property_uid", nullable = false)
	private AgentProperty agentProperty;

	@OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
	private List<CustomerContract> customerContracts = new ArrayList<>();

	@Builder
	private Contract(User user, PropertyType category, BigInteger deposit, BigInteger monthlyRent, BigInteger price,
		LocalDate contractStartDate, LocalDate contractDate,
		LocalDate contractEndDate, ContractStatus status, LocalDate expectedContractEndDate,
		AgentProperty agentProperty) {
		this.user = user;
		this.category = category;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
		this.price = price;
		this.contractStartDate = contractStartDate;
		this.contractDate = contractDate;
		this.contractEndDate = contractEndDate;
		this.status = status;
		this.expectedContractEndDate = expectedContractEndDate;
		this.agentProperty = agentProperty;
	}

	public void modifyContract(PropertyType category, BigInteger deposit, BigInteger monthlyRent, BigInteger price,
		LocalDate contractDate, LocalDate contractStartDate,
		LocalDate contractEndDate, LocalDate expectedContractEndDate, ContractStatus status,
		AgentProperty agentProperty) {
		this.category = category;
		this.deposit = deposit;
		this.monthlyRent = monthlyRent;
		this.price = price;
		this.contractDate = contractDate;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.expectedContractEndDate = expectedContractEndDate;
		this.status = status;
		this.agentProperty = agentProperty;
	}
}
