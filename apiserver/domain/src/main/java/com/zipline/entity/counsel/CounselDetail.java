package com.zipline.entity.counsel;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "counsel_details")
@Entity
public class CounselDetail extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@Column(name = "question", length = 200, nullable = false)
	private String question;

	@Column(name = "answer", length = 200, nullable = false)
	private String answer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "counsel_uid")
	private Counsel counsel;

	public CounselDetail(String question, String answer, Counsel counsel) {
		this.question = question;
		this.answer = answer;
		this.counsel = counsel;
	}
}