package com.zipline.service.region.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zipline.entity.publicitem.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlatRegionDTO {
    private Long cortarNo;
    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level;
    private Long parentCortarNo;

    public FlatRegionDTO() {}

    @JsonCreator
    public FlatRegionDTO(
            @JsonProperty("cortarNo") Long cortarNo,
            @JsonProperty("cortarName") String cortarName,
            @JsonProperty("centerLat") Double centerLat,
            @JsonProperty("centerLon") Double centerLon,
            @JsonProperty("level") Integer level,
            @JsonProperty("parentCortarNo") Long parentCortarNo) {
        this.cortarNo = cortarNo;
        this.cortarName = cortarName;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.level = level;
        this.parentCortarNo = parentCortarNo;
    }

    public static FlatRegionDTO from(Region region) {
        return FlatRegionDTO.builder()
                .cortarNo(region.getCortarNo())
                .cortarName(region.getCortarName())
                .centerLat(region.getCenterLat())
                .centerLon(region.getCenterLon())
                .level(region.getLevel())
                .parentCortarNo(region.getParent() != null ? region.getParent().getCortarNo() : null)
                .build();
    }
}