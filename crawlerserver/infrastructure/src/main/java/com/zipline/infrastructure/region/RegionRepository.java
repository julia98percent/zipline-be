package com.zipline.infrastructure.region;

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
import com.zipline.domain.entity.region.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByCortarNo(Long cortarNo);

    List<Region> findByLevel(Integer level);

    Integer findLevelByCortarNo(Long cortarNo);

    @Query("SELECT r FROM Region r WHERE CONCAT(r.cortarNo, '') LIKE CONCAT(:parentCortarNo, '%')")
    List<Region> findByParentCortarNo(@Param("parentCortarNo") Long parentCortarNo);

    @Query("SELECT r.cortarNo FROM Region r")
    List<Long> findAllCortarNos();

    @Query("SELECT r.cortarNo FROM Region r WHERE r.level = :level")
    List<Long> findCortarNosByLevel(@Param("level") int level);

    @Query("SELECT r.cortarNo FROM Region r WHERE r.level <= :maxLevel")
    List<Long> findCortarNosByLevelLessThanEqual(@Param("maxLevel") int maxLevel);

    @Query("SELECT r.cortarNo FROM Region r WHERE r.level >= :minLevel")
    List<Long> findCortarNosByLevelGreaterThanEqual(@Param("minLevel") int minLevel);

    @Query("SELECT r.cortarNo FROM Region r WHERE r.level BETWEEN :minLevel AND :maxLevel")
    List<Long> findCortarNosByLevelBetween(@Param("minLevel") int minLevel,
                                           @Param("maxLevel") int maxLevel);
}
