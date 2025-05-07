package com.zipline.domain.entity.migration;

import com.zipline.domain.entity.enums.CrawlStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Entity
@Table(name = "migrations")
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no", nullable = false, unique = true)
    private Long cortarNo;

    @Column(name = "naver_status")
    @Enumerated(EnumType.STRING)
    private CrawlStatus naverStatus;

    @Column(name = "naver_last_migrated_at")
    private LocalDateTime naverLastMigratedAt;

    @Column(name = "zigbang_status")
    @Enumerated(EnumType.STRING)
    private CrawlStatus zigbangStatus;

    @Column(name = "zigbang_last_migrated_at")
    private LocalDateTime zigbangLastMigratedAt;


    public Migration(Long id, Long cortarNo, CrawlStatus naverStatus, LocalDateTime naverLastCrawledAt,
                     CrawlStatus zigbangStatus, LocalDateTime zigbangLastCrawledAt) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.naverStatus = naverStatus;
        this.naverLastMigratedAt = naverLastCrawledAt;
        this.zigbangStatus = zigbangStatus;
        this.zigbangLastMigratedAt = zigbangLastCrawledAt;
    }

    public Migration CreateMigration(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.zigbangStatus = CrawlStatus.NEW;
        this.naverLastMigratedAt = null;
        this.zigbangLastMigratedAt = null;
        return this;
    }

    public Migration UpdateMigration(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.zigbangStatus = CrawlStatus.NEW;
        this.naverLastMigratedAt = null;
        this.zigbangLastMigratedAt = null;
        return this;
    }

    /**
     * 네이버 크롤링 상태를 업데이트합니다.
     */
    public Migration updateNaverMigrationStatus(CrawlStatus status) {
        this.naverStatus = status;
        this.naverLastMigratedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 직방 크롤링 상태를 업데이트합니다.
     */
    public Migration updateZigbangMigrationStatus(CrawlStatus status) {
        this.zigbangStatus = status;
        this.zigbangLastMigratedAt = LocalDateTime.now();
        return this;
    }
}