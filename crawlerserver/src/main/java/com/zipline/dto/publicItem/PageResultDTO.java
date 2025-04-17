package com.zipline.dto.publicItem;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 페이지 크롤링 결과를 저장하는 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDTO {
    private int page;
    private List<JsonNode> articles;
    private boolean hasMore;
    private boolean success;
    private boolean proxyError;
    private String error;
    
    /**
     * 성공한 페이지 결과를 생성합니다.
     * 
     * @param page 페이지 번호
     * @param articles 매물 목록
     * @param hasMore 다음 페이지 존재 여부
     * @return PageResultDTO 객체
     */
    public static PageResultDTO success(int page, List<JsonNode> articles, boolean hasMore) {
        return PageResultDTO.builder()
            .page(page)
            .articles(articles)
            .hasMore(hasMore)
            .success(true)
            .proxyError(false)
            .build();
    }
    
    /**
     * 실패한 페이지 결과를 생성합니다.
     * 
     * @param page 페이지 번호
     * @param error 오류 메시지
     * @param isProxyError 프록시 관련 오류 여부
     * @return PageResultDTO 객체
     */
    public static PageResultDTO failure(int page, String error, boolean isProxyError) {
        return PageResultDTO.builder()
            .page(page)
            .articles(List.of())
            .hasMore(false)
            .success(false)
            .proxyError(isProxyError)
            .error(error)
            .build();
    }
    
    /**
     * 페이지 처리 성공 여부를 반환합니다.
     * 
     * @return 성공 여부
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 다음 페이지 존재 여부를 반환합니다.
     * 
     * @return 다음 페이지 존재 여부
     */
    public boolean isHasMore() {
        return hasMore;
    }
    
    /**
     * 프록시 관련 오류 여부를 반환합니다.
     * 
     * @return 프록시 오류 여부
     */
    public boolean isProxyError() {
        return proxyError;
    }
    
    /**
     * 오류 메시지를 반환합니다.
     * 
     * @return 오류 메시지
     */
    public String getError() {
        return error;
    }
    
    /**
     * 매물 목록을 반환합니다.
     * 
     * @return 매물 목록
     */
    public List<JsonNode> getArticles() {
        return articles;
    }
} 