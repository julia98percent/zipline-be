package com.zipline.domain.entity.zigbang;

import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.enums.PropertyCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "zigbang_crawl")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZigBangCrawl {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "geo_hash", length = 12)
    private String geohash;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_category")
    private PropertyCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "zigbang_status")
    private CrawlStatus status;

    @Column(name = "zigbang_last_crawled_at")
    private LocalDateTime lastCrawledAt;

    @Column(name = "error_log", columnDefinition = "LONGTEXT")
    private String errorLog;

    // 생성자
    public static ZigBangCrawl create(String geohash, PropertyCategory category) {
        return ZigBangCrawl.builder()
                .id(buildId(geohash, category))
                .geohash(geohash)
                .category(category)
                .status(CrawlStatus.NEW)
                .build();
    }

    public static String buildId(String geohash, PropertyCategory category) {
        return geohash + "_" + category.name();
    }

    // 상태 업데이트
    public ZigBangCrawl updateStatus(CrawlStatus newStatus) {
        this.status = newStatus;
        this.lastCrawledAt = LocalDateTime.now();
        return this;
    }

    // 에러 로그 추가
    public ZigBangCrawl appendErrorLog(String newError, int maxLength) {
        String currentLog = this.errorLog != null ? this.errorLog : "";
        String updatedLog = currentLog.isEmpty() ?
                String.format("[%s] %s", LocalDateTime.now(), newError) :
                currentLog + "\n" + String.format("[%s] %s", LocalDateTime.now(), newError);

        if (updatedLog.length() > maxLength) {
            updatedLog = updatedLog.substring(updatedLog.length() - maxLength);
        }

        this.errorLog = updatedLog;
        return this;
    }

    // 상태 + 에러 메시지 동시 업데이트
    public ZigBangCrawl errorWithLog(String newError, int maxLength, CrawlStatus status) {
        appendErrorLog(newError, maxLength);
        this.status = status;
        this.lastCrawledAt = LocalDateTime.now();
        return this;
    }
}