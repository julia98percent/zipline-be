package com.zipline.infrastructure.migration;

import com.zipline.domain.entity.migration.Migration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MigrationRepository extends JpaRepository<Migration, Long> {
    
    @Query("SELECT r.cortarNo FROM Migration r")
    List<Long> findAllCortarNos();
    
    Migration findByCortarNo(Long cortarNo);
}
