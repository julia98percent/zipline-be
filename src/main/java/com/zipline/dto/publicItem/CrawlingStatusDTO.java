package com.zipline.dto.publicItem;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 지역별 크롤링 상태를 추적하는 DTO 클래스
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingStatusDTO {
    private String regionName;
    private int currentPage;
    private int totalArticles;
    private boolean isCompleted;
    private Set<Integer> pendingPages;
    private Set<Integer> failedPages;
    private Map<Integer, Integer> retryCount;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    
    /**
     * 새로운 크롤링 상태를 초기화합니다.
     * 
     * @param regionName 지역 이름
     * @return 초기화된 CrawlingStatus 객체
     */
    public static CrawlingStatusDTO initialize(String regionName) {
        return CrawlingStatusDTO.builder()
            .regionName(regionName)
            .currentPage(1)
            .totalArticles(0)
            .isCompleted(false)
            .pendingPages(new HashSet<>())
            .failedPages(new HashSet<>())
            .retryCount(new HashMap<>())
            .startTime(LocalDateTime.now())
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 마지막 업데이트 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastUpdateTime() {
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    /**
     * 크롤링 시작 후 경과 시간을 초 단위로 반환합니다.
     * 
     * @return 경과 시간(초)
     */
    public long getElapsedTimeInSeconds() {
        return ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
    }
} 