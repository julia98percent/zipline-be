package com.zipline.domain.dto.publicitem;

import java.util.List;
import java.util.stream.Collectors;


import com.zipline.domain.entity.publicitem.Region;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class RegionDTO {
    private Long cortarNo;
    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level; // 1: 시/도, 2: 시/군/구, 3: 읍/면/동
    
    /**
     * Region 엔티티로부터 RegionDTO를 생성합니다.
     */
    public static RegionDTO from(Region region) {
        return RegionDTO.builder()
            .cortarNo(region.getCortarNo())
            .cortarName(region.getCortarName())
            .centerLat(region.getCenterLat())
            .centerLon(region.getCenterLon())
            .level(region.getLevel())
            .build();
    }
    
    /**
     * 지역 목록을 DTO 목록으로 변환합니다.
     */
    public static List<RegionDTO> fromList(List<Region> regions) {
        return regions.stream()
            .map(RegionDTO::from)
            .collect(Collectors.toList());
    }
}
