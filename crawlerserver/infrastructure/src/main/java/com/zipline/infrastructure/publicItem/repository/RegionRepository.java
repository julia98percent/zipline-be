package com.zipline.infrastructure.publicItem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.publicitem.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    /**
     * 지역 코드로 지역 정보 조회
     */
    Optional<Region> findByCortarNo(Long cortarNo);

    /**
     * 레벨별 지역 목록 조회
     */
    List<Region> findByLevel(Integer level);

    /**
     * 네이버 크롤링이 필요한 지역 목록 조회
     */
    @Query("SELECT r FROM Region r WHERE r.level = :level " +
           "AND (r.naverLastCrawledAt < :cutoffDate " +
           "OR r.naverStatus != 'COMPLETED')")
    List<Region> findRegionsNeedingUpdateForNaver(@Param("level") int level,
                                                 @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 네이버 크롤링이 필요한 지역 코드 페이징 조회
     */
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level = :level " +
           "AND (r.naverLastCrawledAt < :cutoffDate " +
           "OR r.naverStatus != 'COMPLETED')")
    Page<Long> findRegionsNeedingUpdateForNaverWithPage(@Param("level") int level,
                                                       @Param("cutoffDate") LocalDateTime cutoffDate,
                                                       Pageable pageable);

    /**
     * 상위 지역 코드로 하위 지역 목록 조회
     */
    @Query("SELECT r FROM Region r WHERE CONCAT(r.cortarNo, '') LIKE CONCAT(:parentCortarNo, '%')")
    List<Region> findByParentCortarNo(@Param("parentCortarNo") Long parentCortarNo);

    /**
     * 네이버 크롤링 최종 시간 업데이트
     */
    @Modifying
    @Transactional
    @Query("UPDATE Region r SET r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverLastCrawledAt(@Param("cortarNo") Long cortarNo,
                                 @Param("lastCrawledAt") LocalDateTime lastCrawledAt);

    /**
     * 모든 지역 코드 목록 조회
     */
    @Query("SELECT r.cortarNo FROM Region r")
    List<Long> findAllCortarNos();

    /**
     * 특정 레벨의 지역 코드 목록 조회
     */
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level = :level")
    List<Long> findCortarNosByLevel(@Param("level") int level);

    /**
     * 특정 레벨 이하의 지역 코드 목록 조회
     */
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level <= :maxLevel")
    List<Long> findCortarNosByLevelLessThanEqual(@Param("maxLevel") int maxLevel);

    /**
     * 특정 레벨 이상의 지역 코드 목록 조회
     */
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level >= :minLevel")
    List<Long> findCortarNosByLevelGreaterThanEqual(@Param("minLevel") int minLevel);

    /**
     * 특정 레벨 범위의 지역 코드 목록 조회
     */
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level BETWEEN :minLevel AND :maxLevel")
    List<Long> findCortarNosByLevelBetween(@Param("minLevel") int minLevel,
                                          @Param("maxLevel") int maxLevel);

    /**
     * 네이버 크롤링 상태 업데이트
     */
    @Modifying
    @Transactional
    @Query("UPDATE Region r SET r.naverStatus = :status WHERE r.cortarNo = :cortarNo")
    void updateNaverStatus(@Param("cortarNo") Long cortarNo,
                          @Param("status") CrawlStatus status);

    /**
     * 네이버 크롤링 상태와 최종 시간 함께 업데이트
     */
    @Modifying
    @Transactional
    @Query("UPDATE Region r SET r.naverStatus = :status, r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverStatusAndLastCrawledAt(@Param("cortarNo") Long cortarNo,
                                         @Param("status") CrawlStatus status,
                                         @Param("lastCrawledAt") LocalDateTime lastCrawledAt);
}
