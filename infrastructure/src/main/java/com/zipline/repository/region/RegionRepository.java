package com.zipline.repository.region;

import com.zipline.entity.publicitem.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
	List<Region> findByParentCortarNo(Long parentCortarNo);

	@Query("SELECT concat(gp.cortarName, ' ', p.cortarName, ' ', r.cortarName)  FROM Region r LEFT JOIN Region p ON r.parent.cortarNo = p.cortarNo LEFT JOIN Region gp ON p.parent.cortarNo = gp.cortarNo WHERE r.cortarNo = :cortarNo")
	String findWithParentsByDistrictCode(Long cortarNo);

	@Query("SELECT r.cortarName FROM Region r WHERE r.cortarNo = :cortarNo")
	String findCortarNameByCortarNo(@Param("cortarNo") String cortarNoText);
}