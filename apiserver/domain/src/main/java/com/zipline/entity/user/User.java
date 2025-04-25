package com.zipline.entity.user;

import java.time.LocalTime;

import com.zipline.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid;

	@Column(nullable = false, length = 20, unique = true)
	private String id;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 255)
	private String url;

	@Column(nullable = false)
	private Integer noticeMonth;

	@Column(nullable = false, length = 13)
	private String phoneNo;

	@Column(nullable = false, length = 255)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 5)
	private Authority role;

	@Column(nullable = false)
	private LocalTime noticeTime;

	@Builder
	public User(String id, String password, String name, Authority role,
		String url, String phoneNo, String email, Integer noticeMonth, LocalTime noticeTime
	) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.role = role;
		this.url = url;
		this.phoneNo = phoneNo;
		this.email = email;
		this.noticeMonth = noticeMonth;
		this.noticeTime = noticeTime;
	}

	public void updateInfo(String name, String url, String phoneNo, String email, Integer noticeMonth,
		LocalTime noticeTime) {
		this.name = name;
		this.url = url;
		this.phoneNo = phoneNo;
		this.email = email;
		this.noticeMonth = noticeMonth;
		this.noticeTime = noticeTime;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}