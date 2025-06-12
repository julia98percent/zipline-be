package com.zipline.entity.survey;

import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.user.User;
import com.zipline.global.util.ULIDGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "surveys")
@Entity
public class Survey extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid", nullable = false)
	private Long uid;

	@Column(name = "title", length = 20, nullable = false)
	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid")
	private User user;

	@OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Question> questions = new ArrayList<>();

	@Column(name = "ulid", length = 26, unique = true, nullable = false)
	private String ulid;

	@PrePersist
	private void generateULID() {
		if (ulid == null || ulid.isBlank()) {
			this.ulid = ULIDGenerator.generate();
		}
	}

	public Survey(String title, User user) {
		this.title = title;
		this.user = user;
	}
}
