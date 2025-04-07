package com.zipline.consulation.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zipline.entity.Customer;
import com.zipline.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "counsel")
@Entity
public class Counsel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	private String title;

	private LocalDateTime counselDate;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;

	@OneToMany(mappedBy = "counsel", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<CounselDetail> details = new ArrayList<>();

	public Counsel(String title, LocalDateTime counselDate, LocalDateTime createdAt, LocalDateTime updatedAt,
		LocalDateTime deletedAt, User user, Customer customer) {
		this.title = title;
		this.counselDate = counselDate;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.user = user;
		this.customer = customer;
	}

	public void addDetail(CounselDetail detail) {
		this.details.add(detail);
	}
}
