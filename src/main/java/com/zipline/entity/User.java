package com.zipline.entity;

import com.zipline.dto.UserRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "users")
public class User {

	@Enumerated(EnumType.STRING)
	private Authority role;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid; // PK

	@Column(nullable = false, length = 20, unique = true)
	private String id;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(length = 255)
	private String url;

	@Column
	private Integer birthday;

	@Column
	private Integer noticeMonth;

	@Column(length = 20)
	private String phoneNo;

	@Column(length = 255)
	private String email;

	@Builder
	public User(String id, String password, String name, Authority role,
		String url, Integer birthday, String phoneNo, String email, Integer noticeMonth
	) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.role = role;
		this.url = url;
		this.birthday = birthday;
		this.phoneNo = phoneNo;
		this.email = email;
		this.noticeMonth = noticeMonth;
	}

	public void updateInfo(UserRequestDTO dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.url = dto.getUrl();
		this.birthday = dto.getBirthday();
		this.phoneNo = dto.getPhoneNo();
		this.email = dto.getEmail();
		this.noticeMonth = dto.getNoticeMonth();
	}
}
