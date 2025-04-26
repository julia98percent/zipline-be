package com.zipline.repository.region;

import com.zipline.entity.publicitem.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByParentCortarNo(Long parentCortarNo);
}
