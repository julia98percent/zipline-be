package com.zipline.dto;

import com.zipline.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
	private String uid;
	private String id;
	private String name;
	private String role;
	private String url;
	private Integer birthday;
	private String phoneNo;
	private String email;
	private Integer noticeMonth;

	public static UserResponseDto of(User user) {
		return UserResponseDto.builder()
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
