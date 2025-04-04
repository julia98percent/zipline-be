package com.zipline.repository.publicItem;

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

import com.zipline.entity.publicItem.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByCortarNo(Long cortarNo);
    
    List<Region> findByLevel(Integer level);
    
    @Query("SELECT r FROM Region r WHERE r.level = :level " +
           "AND (r.naverLastCrawledAt < :cutoffDate " +
           "OR r.naverStatus != 'COMPLETED')")
    List<Region> findRegionsNeedingUpdateForNaver(
            @Param("level") int level,
            @Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT r.cortarNo FROM Region r WHERE r.level = :level " +
           "AND (r.naverLastCrawledAt < :cutoffDate " +
           "OR r.naverStatus != 'COMPLETED')")
    Page<Long> findRegionsNeedingUpdateForNaverWithPage(
                    @Param("level") int level,
                    @Param("cutoffDate") LocalDateTime cutoffDate,
                    Pageable pageable);
    
    @Query("SELECT r FROM Region r WHERE CONCAT(r.cortarNo, '') LIKE CONCAT(:parentCortarNo, '%')")
    List<Region> findByParentCortarNo(@Param("parentCortarNo") Long parentCortarNo);

    @Modifying
    @Query("UPDATE Region r SET r.naverLastCrawledAt = :lastCrawledAt WHERE r.cortarNo = :cortarNo")
    void updateNaverLastCrawledAt(@Param("cortarNo") Long cortarNo, @Param("lastCrawledAt") LocalDateTime lastCrawledAt);
} 
