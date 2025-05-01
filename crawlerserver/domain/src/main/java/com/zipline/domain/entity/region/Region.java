package com.zipline.domain.entity.region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zipline.domain.entity.enums.CrawlStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "naver_status")
    @Enumerated(EnumType.STRING)
    private CrawlStatus naverStatus;

    @Column(name = "naver_last_crawled_at")
    private LocalDateTime naverLastCrawledAt;

    @Column(name = "zigbang_status")
    @Enumerated(EnumType.STRING)
    private CrawlStatus zigbangStatus;

    @Column(name = "zigbang_last_crawled_at")
    private LocalDateTime zigbangLastCrawledAt;

    @Column(name = "dabang_status")
    @Enumerated(EnumType.STRING)
    private CrawlStatus dabangStatus;

    @Column(name = "dabang_last_crawled_at")
    private LocalDateTime dabangLastCrawledAt;
    
    /**
     * 네이버 크롤링 상태를 업데이트합니다.
     */
    public Region updateNaverStatus(CrawlStatus status) {
        this.naverStatus = status;
        this.naverLastCrawledAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 직방 크롤링 상태를 업데이트합니다.
     */
    public Region updateZigbangStatus(CrawlStatus status) {
        this.zigbangStatus = status;
        this.zigbangLastCrawledAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 다방 크롤링 상태를 업데이트합니다.
     */
    public Region updateDabangStatus(CrawlStatus status) {
        this.dabangStatus = status;
        this.dabangLastCrawledAt = LocalDateTime.now();
        return this;
    }

    public Region updateCoordinates(Double centerLat, Double centerLon) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        return this;
    }
}
