package com.zipline.entity.enums;

import lombok.Getter;

@Getter
public enum CounselType {
	PURCHASE("매수"),
	SALE("매도"),
	LEASE("임대"),
	RENT("임차"),
	OTHER("기타");

	private final String description;

	CounselType(String description) {
		this.description = description;
	}
}