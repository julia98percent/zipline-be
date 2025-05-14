package com.zipline.domain.entity.naver;

import java.time.LocalDateTime;

import com.zipline.domain.entity.enums.MigrationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "naver_raw_articles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverRawArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "article_id")
    private String articleId;
    
    @Column(name = "cortar_no")
    private Long cortarNo;
    
    @Lob
    @Column(name = "raw_data", columnDefinition = "LONGTEXT")
    private String rawData;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "migration_status")
    @Enumerated(EnumType.STRING)
    private MigrationStatus migrationStatus;
    
    @Column(name = "migration_error")
    private String migrationError;
    
    @Column(name = "migrated_at")
    private LocalDateTime migratedAt;
    
    /**`
     * 마이그레이션 상태를 업데이트합니다.
     */
    public NaverRawArticle updateMigrationStatus(MigrationStatus status) {
        this.migrationStatus = status;
        this.migratedAt = LocalDateTime.now();
        return this;
    }
}
