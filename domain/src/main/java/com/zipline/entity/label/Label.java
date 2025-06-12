package com.zipline.entity.label;

import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.BaseTimeEntity;
import com.zipline.entity.user.User;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "labels")
@Getter
@NoArgsConstructor
public class Label extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_uid", nullable = false)
	private User user;

	@Column(name = "name", nullable = false, length = 10)
	private String name;

	@OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LabelCustomer> labelCustomers = new ArrayList<>();

	public Label(User user, String name) {
		this.user = user;
		this.name = name;
	}

	public void updateName(String name) {
		this.name = name;
	}
}
