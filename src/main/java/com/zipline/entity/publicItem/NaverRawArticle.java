// entity/publicItem/NaverRawArticle.java
package com.zipline.entity.publicItem;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import com.zipline.entity.publicItem.enums.MigrationStatus;
import com.zipline.entity.publicItem.enums.Platform;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "naver_raw_articles", indexes = {
    @Index(name = "idx_raw_article_id", columnList = "articleId"),
    @Index(name = "idx_raw_region_code", columnList = "regionCode"),
    @Index(name = "idx_migration_status", columnList = "migrationStatus")
})
public class NaverRawArticle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String articleId;
    
    @Column(nullable = false)
    private String regionCode;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawData;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationStatus migrationStatus;
    
    @Column(nullable = false)
    private LocalDateTime crawledAt;
    
    @Column
    private LocalDateTime migratedAt;
    
    @Builder
    private NaverRawArticle(String articleId, String regionCode, String rawData, 
                          Platform platform, MigrationStatus migrationStatus, 
                          LocalDateTime crawledAt, LocalDateTime migratedAt) {
        this.articleId = articleId;
        this.regionCode = regionCode;
        this.rawData = rawData;
        this.platform = platform;
        this.migrationStatus = migrationStatus;
        this.crawledAt = crawledAt;
        this.migratedAt = migratedAt;
    }
    
    public void markAsMigrated() {
        this.migrationStatus = MigrationStatus.COMPLETED;
        this.migratedAt = LocalDateTime.now();
    }
    
    public void markAsFailed() {
        this.migrationStatus = MigrationStatus.FAILED;
    }
}
