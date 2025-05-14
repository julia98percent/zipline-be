package com.zipline.domain.entity.crawl;

import com.zipline.domain.entity.enums.CrawlStatus;

import com.zipline.domain.entity.enums.Platform;
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

    @Column(name = "error_log", columnDefinition = "LONGTEXT")
    private String errorLog;

    public Crawl(Long id, Long cortarNo, CrawlStatus naverStatus, LocalDateTime naverLastCrawledAt,
                 String errorLog) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.naverStatus = naverStatus;
        this.naverLastCrawledAt = naverLastCrawledAt;
        this.errorLog = errorLog;
    }

    public Crawl createCrawl(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.naverLastCrawledAt = null;
        return this;
    }

    public Crawl updateCrawl(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = CrawlStatus.NEW;
        this.naverLastCrawledAt = null;
        return this;
    }

    public Crawl updateNaverCrawlStatus(CrawlStatus status) {
        this.naverStatus = status;
        this.naverLastCrawledAt = LocalDateTime.now();
        return this;
    }
    /**
     * 새로운 에러 로그를 기존 로그에 추가합니다.
     *
     * @param newError 새롭게 발생한 에러 메시지
     * @param maxLength 최대 허용 길이 (기본값: 1000)
     * @return Crawl 객체 자기 자신 반환
     */
    //TODO: 공용함수 유틸로 분리?
    public Crawl appendErrorLog(String newError, int maxLength) {
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

    public Crawl errorWithLog(Platform platform, String newError, int maxLength, CrawlStatus status) {
        String currentLog = this.errorLog != null ? this.errorLog : "";

        String updatedLog = currentLog.isEmpty() ?
                String.format("[%s] %s", LocalDateTime.now(), newError) :
                currentLog + "\n" + String.format("[%s] %s", LocalDateTime.now(), newError);

        if (updatedLog.length() > maxLength) {
            updatedLog = updatedLog.substring(updatedLog.length() - maxLength);
        }
        if (platform == Platform.NAVER) {
            this.naverStatus = status;
            this.naverLastCrawledAt = LocalDateTime.now();
        }
        this.errorLog = updatedLog;
        return this;
    }
}
