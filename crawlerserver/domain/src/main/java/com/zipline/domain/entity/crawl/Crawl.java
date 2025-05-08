package com.zipline.domain.entity.crawl;

import com.zipline.domain.entity.enums.CrawlStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "crawls")
@Getter
@Builder
public class Crawl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no", nullable = false, unique = true)
    private Long cortarNo;

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

    public Crawl(Long id, Long cortarNo, CrawlStatus naverStatus, LocalDateTime naverLastCrawledAt,
                 CrawlStatus zigbangStatus, LocalDateTime zigbangLastCrawledAt) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.naverStatus = naverStatus;
        this.naverLastCrawledAt = naverLastCrawledAt;
        this.zigbangStatus = zigbangStatus;
        this.zigbangLastCrawledAt = zigbangLastCrawledAt;

    }

    public Crawl createCrawl(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.zigbangStatus = CrawlStatus.NEW;
        this.naverLastCrawledAt = null;
        this.zigbangLastCrawledAt = null;
        return this;
    }

    public Crawl updateCrawl(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.zigbangStatus = CrawlStatus.NEW;
        this.naverLastCrawledAt = null;
        this.zigbangLastCrawledAt = null;
        return this;
    }

    /**
     * 네이버 크롤링 상태를 업데이트합니다.
     */
    public Crawl updateNaverCrawlStatus(CrawlStatus status) {
        this.naverStatus = status;
        this.naverLastCrawledAt = LocalDateTime.now();
        return this;
    }

    /**
     * 직방 크롤링 상태를 업데이트합니다.
     */
    public Crawl updateZigbangCrawlStatus(CrawlStatus status) {
        this.zigbangStatus = status;
        this.zigbangLastCrawledAt = LocalDateTime.now();
        return this;
    }
}
