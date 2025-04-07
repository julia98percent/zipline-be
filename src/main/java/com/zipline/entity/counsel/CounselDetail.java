package com.zipline.entity.counsel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "counsel_details")
@Entity
public class CounselDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	private String question;

	private String answer;

	@ManyToOne(fetch = FetchType.LAZY)
	private Counsel counsel;

	public CounselDetail(String question, String answer, Counsel counsel) {
		this.question = question;
		this.answer = answer;
		this.counsel = counsel;
	}
}
