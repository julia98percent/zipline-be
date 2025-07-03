package com.zipline.service.publicitem.dto;

import com.zipline.entity.enums.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매물 검색 파라미터")
public class PropertyArticleSearchDTO {
	@Schema(description = "지역 코드", example = "1111010100")
	private String regionCode;

	@Schema(description = "건물명", example = "단독")
	private String buildingName;

	@Schema(description = "건물 유형", example = "단독/다가구")
	private String buildingType;

	@Schema(description = "매물 종류 (SALE, DEPOSIT, MONTHLY)", example = "SALE")
	private Category category;

	@Schema(description = "최소 매매가격", example = "100000")
	private Long minPrice;

	@Schema(description = "최대 매매가격", example = "2000000")
	private Long maxPrice;

	@Schema(description = "최소 보증금", example = "100000")
	private Long minDeposit;

	@Schema(description = "최대 보증금", example = "2000000")
	private Long maxDeposit;

	@Schema(description = "최소 월세", example = "50")
	private Long minMonthlyRent;

	@Schema(description = "최대 월세", example = "2000")
	private Long maxMonthlyRent;

  @Schema(description = "최소 전용면적(㎡)", example = "10.0")
  private Double minNetArea;

  @Schema(description = "최대 전용면적(㎡)", example = "185.0")
  private Double maxNetArea;

  @Schema(description = "최소 공급면적(㎡)", example = "10.0")
  private Double minTotalArea;

  @Schema(description = "최대 공급면적(㎡)", example = "185.0")
  private Double maxTotalArea;

	private String address;
}
