package com.zipline.infrastructure.zigbang;

import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.zigbang.ZigBangMigration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZigBangMigrationRepository extends JpaRepository<ZigBangMigration, String> {
    List<ZigBangMigration> findByStatus(CrawlStatus status);
}
