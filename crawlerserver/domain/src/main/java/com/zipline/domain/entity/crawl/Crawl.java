package com.zipline.domain.entity.crawl;

import com.zipline.domain.entity.enums.CrawlStatus;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawl")
@Getter
@Builder
public class Crawl {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no")
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

    protected Crawl() {
    }

    public Crawl(Long id, Long cortarNo, CrawlStatus naverStatus, LocalDateTime naverLastCrawledAt,
                 CrawlStatus zigbangStatus, LocalDateTime zigbangLastCrawledAt) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.naverStatus = naverStatus;
        this.naverLastCrawledAt = naverLastCrawledAt;
        this.zigbangStatus = zigbangStatus;
        this.zigbangLastCrawledAt = zigbangLastCrawledAt;

    }

    public Crawl CreateCrawl(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.zigbangStatus = CrawlStatus.NEW;
        this.naverLastCrawledAt = null;
        this.zigbangLastCrawledAt = null;
        return this;
    }

    public Crawl UpdateCrawl(Long cortarNo) {
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
