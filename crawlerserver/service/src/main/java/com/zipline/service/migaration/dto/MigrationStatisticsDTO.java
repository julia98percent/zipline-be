package com.zipline.service.migaration.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 마이그레이션 통계 정보를 담는 DTO
 */
@Getter
@Builder
@ToString
public class MigrationStatisticsDTO {
    private Long regionCode;
    private long totalArticles;
    private long pendingArticles;
    private long completedArticles;
    private long failedArticles;
    private double completionRate;
    private double failureRate;
    private LocalDateTime timestamp;
    
    /**
     * 전체 마이그레이션 통계 정보를 생성합니다.
     */
    public static MigrationStatisticsDTO of(
            long totalArticles, 
            long pendingArticles, 
            long completedArticles, 
            long failedArticles) {
        
        double completionRate = totalArticles > 0 ? (double) completedArticles / totalArticles * 100 : 0;
        double failureRate = totalArticles > 0 ? (double) failedArticles / totalArticles * 100 : 0;
        
        return MigrationStatisticsDTO.builder()
            .totalArticles(totalArticles)
            .pendingArticles(pendingArticles)
            .completedArticles(completedArticles)
            .failedArticles(failedArticles)
            .completionRate(completionRate)
            .failureRate(failureRate)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * 특정 지역의 마이그레이션 통계 정보를 생성합니다.
     */
    public static MigrationStatisticsDTO ofRegion(
            Long regionCode,
            long totalArticles, 
            long pendingArticles, 
            long completedArticles, 
            long failedArticles) {
        
        double completionRate = totalArticles > 0 ? (double) completedArticles / totalArticles * 100 : 0;
        double failureRate = totalArticles > 0 ? (double) failedArticles / totalArticles * 100 : 0;
        
        return MigrationStatisticsDTO.builder()
            .regionCode(regionCode)
            .totalArticles(totalArticles)
            .pendingArticles(pendingArticles)
            .completedArticles(completedArticles)
            .failedArticles(failedArticles)
            .completionRate(completionRate)
            .failureRate(failureRate)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
