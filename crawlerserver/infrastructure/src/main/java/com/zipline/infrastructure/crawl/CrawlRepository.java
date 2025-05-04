package com.zipline.infrastructure.crawl;

import com.zipline.domain.entity.crawl.Crawl;
import com.zipline.domain.entity.enums.CrawlStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CrawlRepository extends JpaRepository<Crawl, Long> {
    List<Crawl> findByRegionCode(String regionCode);
    Optional<Crawl> findByArticleId(String articleId);
    List<Crawl> findByStatus(CrawlStatus status);
    List<Crawl> findByStatusAndRegionCode(CrawlStatus status, String regionCode);

    @Query("UPDATE Crawl r SET r.naverStatus = :status WHERE r.cortarNo = :cortarNo")
    void updateNaverCrawlStatus(@Param("cortarNo") Long cortarNo,
                           @Param("status") CrawlStatus status);

    @Query("UPDATE Crawl r SET r.naverStatus = :status, r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverCrawlStatusAndLastCrawledAt(@Param("cortarNo") Long cortarNo,
                                           @Param("status") CrawlStatus status,
                                           @Param("lastCrawledAt") LocalDateTime lastCrawledAt);

    /**
     * 네이버 크롤링이 필요한 지역 목록 조회
     */
    @Query("SELECT r FROM Crawl r WHERE r.level = :level " +
            "AND (r.naverLastCrawledAt < :cutoffDate " +
            "OR r.naverStatus != 'COMPLETED')")
    List<Crawl> findRegionsNeedingCrawlingUpdateForNaver(@Param("level") int level,
                                                  @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 네이버 크롤링이 필요한 지역 코드 페이징 조회
     */
    @Query("SELECT r.cortarNo FROM Crawl r WHERE r.level = :level " +
            "AND (r.naverLastCrawledAt < :cutoffDate " +
            "OR r.naverStatus != 'COMPLETED')")
    Page<Long> findRegionsNeedingCrawlingUpdateForNaverWithPage(@Param("level") int level,
                                                        @Param("cutoffDate") LocalDateTime cutoffDate,
                                                        Pageable pageable);

    /**
     * 네이버 크롤링 최종 시간 업데이트
     */
    @Query("UPDATE Crawl r SET r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverLastCrawledAt(@Param("cortarNo") Long cortarNo,
                                  @Param("lastCrawledAt") LocalDateTime lastCrawledAt);

}
