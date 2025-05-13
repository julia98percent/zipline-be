package com.zipline.infrastructure.zigbang;

import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.zigbang.ZigBangCrawl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZigBangCrawlRepository extends JpaRepository<ZigBangCrawl, String> {
    List<ZigBangCrawl> findByStatus(MigrationStatus status);
    ZigBangCrawl findByGeohash(String geohash);
}
