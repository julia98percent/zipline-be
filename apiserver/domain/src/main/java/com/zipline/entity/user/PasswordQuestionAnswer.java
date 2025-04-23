package com.zipline.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_question_answers")
@Getter
@NoArgsConstructor
public class PasswordQuestionAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "password_question_uid", nullable = false)
	private PasswordQuestion passwordQuestion;

	@Column(nullable = false)
	private String answer;

	public PasswordQuestionAnswer(User user, PasswordQuestion passwordQuestion, String answer) {
		this.user = user;
		this.passwordQuestion = passwordQuestion;
		this.answer = answer;
	}

	public static PasswordQuestionAnswer create(User user, PasswordQuestion question, String answer) {
		return new PasswordQuestionAnswer(user, question, answer);
	}
}