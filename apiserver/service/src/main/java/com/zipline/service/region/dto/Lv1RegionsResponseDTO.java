package com.zipline.service.region.dto;

import com.zipline.entity.publicitem.Region;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class Lv1RegionsResponseDTO {

    @Builder
    @Getter
    public static class Lv1RegionDTO {
        private Long cortarNo;
        private String cortarName;
        private Double centerLat;
        private Double centerLon;
        private Long parentCortarNo;
    }

    private List<Lv1RegionDTO> regions;

    public static Lv1RegionsResponseDTO from(List<Region> regionInfos) {
        List<Lv1RegionDTO> lv1RegionDTOs = regionInfos.stream()
                .map(info -> Lv1RegionDTO.builder()
                        .cortarNo(info.getCortarNo())
                        .cortarName(info.getCortarName())
                        .centerLat(info.getCenterLat())
                        .centerLon(info.getCenterLon())
                        .parentCortarNo(info.getParent() != null ? info.getParent().getCortarNo() : null)
                        .build())
                .collect(Collectors.toList());

        return Lv1RegionsResponseDTO.builder()
                .regions(lv1RegionDTOs)
                .build();
    }
}