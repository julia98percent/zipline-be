package com.zipline.service.region.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.zipline.domain.entity.region.Region;

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

    //todo: 상태관리 이전대애서 구현필요
    public static Region createKoreaRegion() {
        return Region.builder()
                .cortarNo(0L)
                .cortarName("대한민국")
                .level(0)
                .centerLat(36.5)
                .centerLon(127.5)
                .parent(null)
                //.naverStatus(CrawlStatus.NEW)
                //.zigbangStatus(CrawlStatus.NEW)
                .build();
    }

    public static RegionDTO fromJsonNode(JsonNode node) {
        return RegionDTO.builder()
                .cortarNo(node.path("cortarNo").asLong())
                .cortarName(node.path("cortarName").asText())
                .centerLat(node.path("centerLat").asDouble())
                .centerLon(node.path("centerLon").asDouble())
                .build();
    }

    public Region toEntity(int level, Region parent) {
        return Region.builder()
                .cortarNo(this.getCortarNo())
                .cortarName(this.getCortarName())
                .level(level)
                .centerLat(this.getCenterLat())
                .centerLon(this.getCenterLon())
                .parent(parent)
                //.naverStatus(CrawlStatus.NEW)
                //.zigbangStatus(CrawlStatus.NEW)
                .build();
    }
}
