package com.zipline.repository.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.publicitem.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
	List<Region> findByParentCortarNo(Long parentCortarNo);

	@Query("SELECT concat(gp.cortarName, ' ', p.cortarName, ' ', r.cortarName)  FROM Region r LEFT JOIN Region p ON r.parent.cortarNo = p.cortarNo LEFT JOIN Region gp ON p.parent.cortarNo = gp.cortarNo WHERE r.cortarNo = :cortarNo")
	String findWithParentsByDistrictCode(Long cortarNo);
}
