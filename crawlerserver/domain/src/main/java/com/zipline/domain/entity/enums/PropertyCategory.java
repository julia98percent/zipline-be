package com.zipline.domain.entity.enums;


import lombok.Getter;

@Getter
public enum PropertyCategory {
	ONE_ROOM("oneroom"),     // 원룸
	TWO_ROOM("tworoom"),     // 투룸
	APARTMENT("apt"),    // 아파트
	VILLA("villa"),        // 빌라
	HOUSE("house"),        // 주택
	OFFICETEL("officetel"),    // 오피스텔
	COMMERCIAL("store");    // 상가

	private final String apiPath;

	PropertyCategory(String apiPath) {
		this.apiPath = apiPath;
	}

	public static boolean contains(String category) {
		for (PropertyCategory c : values()) {
			if (c.name().equalsIgnoreCase(category)) {
				return true;
			}
		}
		return false;
	}

	public boolean supportsSaleType() {
		return this == VILLA || this == OFFICETEL;
	}
}

