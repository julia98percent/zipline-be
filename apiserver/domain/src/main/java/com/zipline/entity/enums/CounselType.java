package com.zipline.entity.enums;

import java.util.Arrays;

import com.zipline.global.exception.counsel.CounselException;
import com.zipline.global.exception.counsel.errorcode.CounselErrorCode;

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

	public static CounselType from(String type) {
		return Arrays.stream(CounselType.values())
			.filter(e -> e.description.equals(type))
			.findFirst()
			.orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));
	}
}