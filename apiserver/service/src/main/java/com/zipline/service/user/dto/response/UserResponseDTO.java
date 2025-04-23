package com.zipline.service.user.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.zipline.entity.survey.Survey;
import com.zipline.entity.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {
	private String uid;
	private String name;
	private String url;
	private String phoneNo;
	private String email;
	private Integer noticeMonth;
	private LocalTime noticeTime;
	private String surveyTitle;
	private LocalDateTime surveyCreatedAt;

	public static UserResponseDTO userSurvey(User user, Survey survey) {
		return UserResponseDTO.builder()
			.uid(String.valueOf(user.getUid()))
			.name(user.getName())
			.url(user.getUrl())
			.phoneNo(user.getPhoneNo())
			.email(user.getEmail())
			.noticeMonth(user.getNoticeMonth())
			.noticeTime(user.getNoticeTime())
			.surveyTitle(survey.getTitle())
			.surveyCreatedAt(survey.getCreatedAt())
			.build();
	}
}
