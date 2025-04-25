package com.zipline.repository.region;

import com.zipline.entity.publicitem.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    /**
     * 레벨별 지역 목록 조회
     */
    List<Region> findByLevel(Integer level);

    List<Region> findByParentCortarNo(Long parentCortarNo);
}
