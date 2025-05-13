package com.zipline.domain.entity.zigbang;

import com.zipline.domain.entity.enums.MigrationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "geo_hash_migration")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZigBangMigration {

    @Id
    @Column(name = "geo_hash")
    private String geohash;

    @Column(name = "zigbang_status")
    @Enumerated(EnumType.STRING)
    private MigrationStatus status;

    @Column(name = "zigbang_last_migrated_at")
    private LocalDateTime lastMigratedAt;

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

}