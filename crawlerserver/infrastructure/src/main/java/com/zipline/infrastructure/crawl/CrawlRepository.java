package com.zipline.infrastructure.crawl;

import com.zipline.domain.entity.crawl.Crawl;
import com.zipline.domain.entity.enums.CrawlStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

public interface CrawlRepository extends JpaRepository<Crawl, Long> {
    Crawl findByCortarNo(Long cortarNo);

    @Transactional
    @Modifying
    @Query("UPDATE Crawl r SET r.naverStatus = :status WHERE r.cortarNo = :cortarNo")
    void updateNaverCrawlStatus(@Param("cortarNo") Long cortarNo,
                                @Param("status") CrawlStatus status);

    @Transactional
    @Modifying
    @Query("UPDATE Crawl r SET r.naverStatus = :status, r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverCrawlStatusAndLastCrawledAt(@Param("cortarNo") Long cortarNo,
                                                @Param("status") CrawlStatus status,
                                                @Param("lastCrawledAt") LocalDateTime lastCrawledAt);

    @Query("SELECT r.cortarNo FROM Crawl r WHERE r.naverLastCrawledAt IS NULL OR r.naverLastCrawledAt < :cutoffDate OR r.naverStatus IN ('FAILED', 'PROCESSING')")
    Page<Long> findRegionsNeedingCrawlingUpdateForNaverWithPage(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Crawl m SET m.errorLog = :errorLog WHERE m.cortarNo = :cortarNo")
    void updateErrorLog(@Param("cortarNo") Long cortarNo, @Param("errorLog") String errorLog);

//    /**
//     * 네이버 크롤링이 필요한 지역 목록 조회
//     */
//    @Query("SELECT r FROM Crawl r WHERE r.naverLastCrawledAt < :cutoffDate OR r.naverStatus IN ('FAILED', 'PROCESSING')")
//    List<Crawl> findRegionsNeedingCrawlingUpdateForNaver(@Param("cutoffDate") LocalDateTime cutoffDate);
//
//    /**
//     * 네이버 크롤링 최종 시간 업데이트
//     */
//    @Transactional
//    @Modifying
//    @Query("UPDATE Crawl r SET r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
//    void updateNaverLastCrawledAt(@Param("cortarNo") Long cortarNo,
//                                  @Param("lastCrawledAt") LocalDateTime lastCrawledAt);
//
}