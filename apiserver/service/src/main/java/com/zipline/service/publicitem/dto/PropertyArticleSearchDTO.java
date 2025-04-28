package com.zipline.service.publicitem.dto;

import com.zipline.entity.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매물 검색 파라미터")
public class PropertyArticleSearchDTO {
    @Schema(description = "지역 코드", example = "11680")
    private String regionCode;

    @Schema(description = "건물명", example = "자이아파트")
    private String buildingName;

    @Schema(description = "건물 유형", example = "아파트")
    private String buildingType;

    @Schema(description = "매물 종류 (SALE, JEONSE, MONTHLY)", example = "SALE")
    private Category category;

    @Schema(description = "최소 매매가격", example = "100000000")
    private Long minPrice;

    @Schema(description = "최대 매매가격", example = "500000000")
    private Long maxPrice;

    @Schema(description = "최소 보증금", example = "10000000")
    private Long minDeposit;

    @Schema(description = "최대 보증금", example = "50000000")
    private Long maxDeposit;

    @Schema(description = "최소 월세", example = "500000")
    private Long minMonthlyRent;

    @Schema(description = "최대 월세", example = "2000000")
    private Long maxMonthlyRent;

    @Schema(description = "최소 전용면적(㎡)", example = "60.0")
    private Double minArea;

    @Schema(description = "최대 전용면적(㎡)", example = "85.0")
    private Double maxArea;
}
