package com.zipline.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeoCodeResultVO {
	private String legalDistrictCode;
	private String longitude;
	private String latitude;
}
