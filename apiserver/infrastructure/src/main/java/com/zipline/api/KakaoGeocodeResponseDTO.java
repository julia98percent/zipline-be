package com.zipline.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class KakaoGeocodeResponseDTO {

	private List<Document> documents;

	@Getter
	public static class Document {
		@JsonProperty("x")
		private String longitude;
		@JsonProperty("y")
		private String latitude;

		@JsonProperty("address")
		private Address address;

		@JsonProperty("road_address")
		private RoadAddress roadAddress;

		@Getter
		public static class Address {
			@JsonProperty("address_name")
			private String jibunAddressName;
			@JsonProperty("b_code")
			private String legalDistrictCode;

			@JsonProperty("region_3depth_h_name")
			private String dongHName;

			@JsonProperty("region_3depth_name")
			private String dongName;
		}

		@Getter
		public static class RoadAddress {
			@JsonProperty("address_name")
			private String roadAddressName;
		}
	}
}
