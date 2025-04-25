package com.zipline.service.customer.dto.request;

import java.math.BigInteger;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerModifyRequestDTO {
	@Schema(description = "사용자 이름", example = "홍길동", required = true)
	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String name;

	@Schema(description = "전화번호", example = "010-1234-5678", required = true)
	@Pattern(regexp = "^(\\d{3})-(\\d{3,4})-(\\d{4})$", message = "전화번호 형식이 올바르지 않습니다.")
	private String phoneNo;

	@Schema(description = "라벨 UID 목록", example = "[1, 2]")
	private List<Long> labelUids;

	@Schema(description = "통신사", example = "SKT")
	private String telProvider;

	@Schema(description = "지역 코드", example = "110000000")
	private String legalDistrictCode;

	@Schema(description = "최소 임대료", example = "100000")
	@DecimalMin(value = "0", message = "최소 임대료는 0 이상이어야 합니다.")
	private BigInteger minRent;

	@Schema(description = "최대 임대료", example = "500000")
	@DecimalMax(value = "100000000000", message = "최대 임대료는 100000000000 이하여야 합니다.")
	private BigInteger maxRent;

	@Schema(description = "유입 경로", example = "직방")
	private String trafficSource;

	@Schema(description = "임대인 여부", example = "true")
	private boolean isLandlord;

	@Schema(description = "임차인 여부", example = "true")
	private boolean isTenant;

	@Schema(description = "매수인 여부", example = "false")
	private boolean isBuyer;

	@Schema(description = "매도인 여부", example = "false")
	private boolean isSeller;

	@Schema(description = "최대 가격", example = "100000000")
	@DecimalMin(value = "0", message = "최대 가격은 0 이상이어야 합니다.")
	private BigInteger maxPrice;

	@Schema(description = "최소 가격", example = "5000000")
	@DecimalMin(value = "0", message = "최소 가격은 0 이상이어야 합니다.")
	private BigInteger minPrice;

	@Schema(description = "최소 보증금", example = "1000000")
	@DecimalMin(value = "0", message = "최소 보증금은 0 이상이어야 합니다.")
	private BigInteger minDeposit;

	@Schema(description = "최대 보증금", example = "10000000")
	@DecimalMax(value = "1000000000", message = "최대 보증금은 1000000000 이하여야 합니다.")
	private BigInteger maxDeposit;

	@Schema(description = "생년월일", example = "20021123")
	private String birthday;
}
