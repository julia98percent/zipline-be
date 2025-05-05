package com.zipline.domain.entity.migration;
import com.zipline.domain.entity.enums.CrawlStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Table(name = "migrations")
@Getter
@Builder
public class Migration {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no")
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

    protected Migration() {
    }

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