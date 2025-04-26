package com.zipline.service.publicitem.dto;

import com.zipline.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyArticleSearchDTO {
    private String regionCode;
    private String buildingName;
    private String buildingType;
    private Category category;
    private Long minPrice;
    private Long maxPrice;
    private Long minDeposit;
    private Long maxDeposit;
    private Long minMonthlyRent;
    private Long maxMonthlyRent;
    private Double minArea;
    private Double maxArea;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}

