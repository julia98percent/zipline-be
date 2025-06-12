package com.zipline.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_questions")
@Getter
@NoArgsConstructor
public class PasswordQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@Column(nullable = false, unique = true)
	private String question;
	
	public PasswordQuestion(String question) {
		this.question = question;
	}
}