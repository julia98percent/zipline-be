package com.zipline.auth.entity;

import java.time.LocalDate;

import com.zipline.auth.dto.AgentRequestDto;

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
@Table(name = "agents" )
public class Agent {

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uid; // PK

	@Column(nullable = false)
	private String id;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column
	private String role;

	@Column
	private String url;

	@Column
	private LocalDate birthday;

	@Column
	private String qr;

	@Column
	private String phoneNo;

	@Column
	private String email;

	@Column
	private String certNo;

	@Column
	private LocalDate certIssueDate;

	@Builder
	public Agent(Long agencyId, String id, String password, String name, String role,
		String url, LocalDate birthday, String qr, String phoneNo, String email,
		String certNo, LocalDate certIssueDate, Authority authority) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.role = role;
		this.url = url;
		this.birthday = birthday;
		this.qr = qr;
		this.phoneNo = phoneNo;
		this.email = email;
		this.certNo = certNo;
		this.certIssueDate = certIssueDate;
		this.authority = authority;
	}

	public void updateInfo(AgentRequestDto dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.role = dto.getRole();
		this.url = dto.getUrl();
		this.birthday = dto.getBirthday();
		this.qr = dto.getQr();
		this.phoneNo = dto.getPhoneNo();
		this.email = dto.getEmail();
		this.certNo = dto.getCertNo();
		this.certIssueDate = dto.getCertIssueDate();
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}
}
