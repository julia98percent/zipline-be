package com.zipline.entity.publicItem;


import com.zipline.entity.publicItem.enums.MigrationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 네이버 부동산 API에서 수집한 원본 매물 데이터를 저장하는 엔티티
 */
@Entity
@Table(name = "naver_raw_articles", indexes = {
    @Index(name = "idx_cortar_no", columnList = "cortarNo"),
    @Index(name = "idx_article_id", columnList = "articleId", unique = true),
    @Index(name = "idx_migration_status", columnList = "migrationStatus")
})
@Data
@NoArgsConstructor
public class NaverRawArticle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 네이버 부동산 매물 ID
     */
    @Column(nullable = false)
    private String articleId;
    
    /**
     * 지역 코드 (코타르 번호)
     */
    @Column(nullable = false)
    private Long cortarNo;
    
    /**
     * 원본 JSON 데이터 (전체 응답)
     */
    @Column(columnDefinition = "TEXT")
    private String rawData;
    
    /**
     * 데이터 마이그레이션 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationStatus migrationStatus = MigrationStatus.PENDING;
    
    /**
     * 마이그레이션 처리 시간
     */
    private LocalDateTime migratedAt;
    
    /**
     * 마이그레이션 실패 메시지
     */
    @Column(columnDefinition = "TEXT")
    private String migrationError;
    
    /**
     * 데이터 수집 시간
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 데이터 업데이트 시간
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
