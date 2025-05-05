package com.zipline.infrastructure.migration;

import com.zipline.domain.entity.migration.Migration;
import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.region.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface MigrationRepository {
    List<Migration> findByRegionCode(String regionCode);
    Optional<Migration> findByArticleId(String articleId);
    List<Migration> findByStatus(MigrationStatus status);
    List<Migration> findByStatusAndRegionCode(MigrationStatus status, String regionCode);

    @Query("UPDATE Crawl r SET r.naverStatus = :status WHERE r.cortarNo = :cortarNo")
    void updateNaverMigrationStatus(@Param("cortarNo") Long cortarNo,
                           @Param("status") CrawlStatus status);

    @Query("UPDATE Crawl r SET r.naverStatus = :status, r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverStatusAndLastMigratededAt(@Param("cortarNo") Long cortarNo,
                                           @Param("status") CrawlStatus status,
                                           @Param("lastCrawledAt") LocalDateTime lastCrawledAt);

    /**
     * 네이버 크롤링이 필요한 지역 목록 조회
     */
    @Query("SELECT r FROM Crawl r WHERE r.level = :(r.naverLastCrawledAt < :cutoffDate " + "OR r.naverStatus != 'COMPLETED')")
    List<Region> findMigrationsNeedingUpdateForNaver(@Param("level") int level,
                                                  @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 네이버 크롤링이 필요한 지역 코드 페이징 조회
     */
    @Query("SELECT r.cortarNo FROM Crawl r WHERE(r.naverLastCrawledAt < :cutoffDate " + "OR r.naverStatus != 'COMPLETED')")
    Page<Long> findMigrationsNeedingUpdateForNaverWithPage(@Param("level") int level,
                                                        @Param("cutoffDate") LocalDateTime cutoffDate,
                                                        Pageable pageable);

    /**
     * 네이버 크롤링 최종 시간 업데이트
     */
    @Query("UPDATE Crawl r SET r.naverLastMigratededAt = :lastMigratedAt WHERE r.cortarNo = :cortarNo")
    void updateNaverLastMigratedAt(@Param("cortarNo") Long cortarNo,
                                  @Param("lastMigratedAt") LocalDateTime lastCrawledAt);

}
