package com.zipline.service.proxy.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 지역별 크롤링 상태를 추적하는 DTO 클래스
 */
@Getter
@Builder
@ToString
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
     * 
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO updateLastUpdateTime() {
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(this.pendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }

    /**
     * 크롤링 시작 후 경과 시간을 초 단위로 반환합니다.
     *
     * @return 경과 시간(초)
     */
    public long getElapsedTimeInSeconds() {
        return ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
    }
    
    /**
     * 현재 페이지를 업데이트합니다.
     * 
     * @param page 새 페이지 번호
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO updateCurrentPage(int page) {
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(page)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(this.pendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 수집된 매물 수를 증가시킵니다.
     * 
     * @param count 증가시킬 매물 수
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO incrementTotalArticles(int count) {
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles + count)
            .isCompleted(this.isCompleted)
            .pendingPages(this.pendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 대기 중인 페이지를 추가합니다.
     * 
     * @param page 페이지 번호
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO addPendingPage(int page) {
        Set<Integer> updatedPendingPages = new HashSet<>(this.pendingPages);
        updatedPendingPages.add(page);
        
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(updatedPendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 대기 중인 페이지를 제거합니다.
     * 
     * @param page 페이지 번호
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO removePendingPage(int page) {
        Set<Integer> updatedPendingPages = new HashSet<>(this.pendingPages);
        updatedPendingPages.remove(page);
        
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(updatedPendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 실패한 페이지를 추가합니다.
     * 
     * @param page 페이지 번호
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO addFailedPage(int page) {
        Set<Integer> updatedFailedPages = new HashSet<>(this.failedPages);
        updatedFailedPages.add(page);
        
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(this.pendingPages)
            .failedPages(updatedFailedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 페이지 재시도 횟수를 증가시킵니다.
     * 
     * @param page 페이지 번호
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO incrementRetryCount(int page) {
        Map<Integer, Integer> updatedRetryCount = new HashMap<>(this.retryCount);
        updatedRetryCount.put(page, updatedRetryCount.getOrDefault(page, 0) + 1);
        
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(this.isCompleted)
            .pendingPages(this.pendingPages)
            .failedPages(this.failedPages)
            .retryCount(updatedRetryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 크롤링 완료 상태로 설정합니다.
     * 
     * @return 업데이트된 DTO 객체
     */
    public CrawlingStatusDTO markAsCompleted() {
        return CrawlingStatusDTO.builder()
            .regionName(this.regionName)
            .currentPage(this.currentPage)
            .totalArticles(this.totalArticles)
            .isCompleted(true)
            .pendingPages(this.pendingPages)
            .failedPages(this.failedPages)
            .retryCount(this.retryCount)
            .startTime(this.startTime)
            .lastUpdateTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 페이지 재시도 횟수를 반환합니다.
     * 
     * @param page 페이지 번호
     * @return 재시도 횟수
     */
    public int getRetryCount(int page) {
        return this.retryCount.getOrDefault(page, 0);
    }
    
    /**
     * 대기 중인 페이지 수를 반환합니다.
     * 
     * @return 대기 중인 페이지 수
     */
    public int getPendingPageCount() {
        return this.pendingPages.size();
    }
    
    /**
     * 실패한 페이지 수를 반환합니다.
     * 
     * @return 실패한 페이지 수
     */
    public int getFailedPageCount() {
        return this.failedPages.size();
    }
}
