package com.zipline.entity.survey;

import java.time.LocalDateTime;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "survey_responses")
@Entity
public class SurveyResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_uid")
	private Survey survey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_uid")
	private Customer customer;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public SurveyResponse(Survey survey, Customer customer, LocalDateTime createdAt) {
		this.survey = survey;
		this.customer = customer;
		this.createdAt = createdAt;
	}
}
