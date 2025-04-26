package com.zipline.entity.publicitem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 지역 정보를 저장하는 엔티티
 * - 시/도, 시/군/구, 읍/면/동 계층 구조 관리
 * - 각 플랫폼별 업데이트 날짜 추적
 */
@Entity
@Table(name = "regions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
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
}
