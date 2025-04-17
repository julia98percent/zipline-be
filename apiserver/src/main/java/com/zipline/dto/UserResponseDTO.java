package com.zipline.dto;

import java.time.LocalDateTime;

import com.zipline.entity.User;
import com.zipline.survey.entity.Survey;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {
	private String uid;
	private String id;
	private String name;
	private String role;
	private String url;
	private Integer birthday;
	private String phoneNo;
	private String email;
	private Integer noticeMonth;
	private String surveyTitle;
	private LocalDateTime surveyCreatedAt;

	public static UserResponseDTO userSurvey(User user, Survey survey) {
		return UserResponseDTO.builder()
			.uid(String.valueOf(user.getUid()))
			.id(user.getId())
			.name(user.getName())
			.role(String.valueOf(user.getRole()))
			.url(user.getUrl())
			.birthday(user.getBirthday())
			.phoneNo(user.getPhoneNo())
			.email(user.getEmail())
			.noticeMonth(user.getNoticeMonth())
			.surveyTitle(survey.getTitle())
			.surveyCreatedAt(survey.getCreatedAt())
			.build();
	}

	public static UserResponseDTO of(User user) {
		return UserResponseDTO.builder()
			.uid(String.valueOf(user.getUid()))
			.id(user.getId())
			.name(user.getName())
			.role(String.valueOf(user.getRole()))
			.url(user.getUrl())
			.birthday(user.getBirthday())
			.phoneNo(user.getPhoneNo())
			.email(user.getEmail())
			.noticeMonth(user.getNoticeMonth())
			.build();
	}
}
