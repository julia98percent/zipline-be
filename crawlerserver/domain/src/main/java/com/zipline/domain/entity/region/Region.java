package com.zipline.domain.entity.region;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

/**
 * 지역 정보를 저장하는 엔티티
 * - 시/도, 시/군/구, 읍/면/동 계층 구조 관리
 * - 각 플랫폼별 업데이트 날짜 추적
 */
@Entity
@Table(name = "regions")
@Getter
@Builder
public class Region {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no")
    private Long cortarNo;

    @Column(name = "cortar_name")
    private String cortarName;

    @Column(name = "center_lat")
    private Double centerLat;

    @Column(name = "center_lon")
    private Double centerLon;

    @Column(name = "level")
    private Integer level; // 1: 시/도, 2: 시/군/구, 3: 읍/면/동

    @ManyToOne
    @JoinColumn(name = "parent_cortar_no")
    private Region parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Region> children = new ArrayList<>();

    protected Region() {
    }

    public Region(Long id, Long cortarNo, String cortarName, Double centerLat, Double centerLon,
                 Integer level, Region parent, List<Region> children) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.cortarName = cortarName;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.level = level;
        this.parent = parent;
        this.children = children != null ? children : new ArrayList<>();
    }

    public Region updateCoordinates(Double centerLat, Double centerLon) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        return this;
    }
}

