package com.zipline.domain.dto.publicitem;

import java.time.LocalDateTime;


import com.zipline.domain.entity.enums.CrawlStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 지역별 크롤링 상태 정보를 담는 DTO 클래스
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CrawlStatusDTO {
    private Long regionCode;
    private String regionName;
    private CrawlStatus status;
    private LocalDateTime lastCrawledAt;
    private long totalArticles;
    private long pendingArticles;
    private long completedArticles;
    private long failedArticles;
    
    /**
     * 진행률을 계산합니다.
     * 
     * @return 진행률 (0-100%)
     */
    public int getProgressPercentage() {
        if (totalArticles == 0) {
            return 0;
        }
        return (int) ((completedArticles * 100) / totalArticles);
    }
    
    /**
     * 크롤링이 완료되었는지 확인합니다.
     * 
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return status == CrawlStatus.COMPLETED;
    }
    
    /**
     * 크롤링이 실패했는지 확인합니다.
     * 
     * @return 실패 여부
     */
    public boolean isFailed() {
        return status == CrawlStatus.FAILED;
    }
    
    /**
     * 크롤링이 진행 중인지 확인합니다.
     * 
     * @return 진행 중 여부
     */
    public boolean isProcessing() {
        return status == CrawlStatus.PROCESSING;
    }
}
