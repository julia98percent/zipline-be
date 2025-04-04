package com.zipline.dto.publicItem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RegionDTO {
    private Long cortarNo;
    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level; // 1: 시/도, 2: 시/군/구, 3: 읍/면/동
} 