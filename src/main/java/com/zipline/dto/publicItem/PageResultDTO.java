package com.zipline.dto.publicItem;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 페이지 크롤링 결과를 저장하는 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDTO {
    private int page;
    private boolean success;
    private List<JsonNode> articles;
    private boolean hasMore;
    private String error;
    private boolean isProxyError;
    
    /**
     * 성공한 페이지 결과를 생성합니다.
     * 
     * @param page 페이지 번호
     * @param articles 매물 목록
     * @param hasMore 더 많은 데이터가 있는지 여부
     * @return 성공한 PageResult 객체
     */
    public static PageResultDTO success(int page, List<JsonNode> articles, boolean hasMore) {
        return PageResultDTO.builder()
            .page(page)
            .success(true)
            .articles(articles)
            .hasMore(hasMore)
            .build();
    }
    
    /**
     * 실패한 페이지 결과를 생성합니다.
     * 
     * @param page 페이지 번호
     * @param error 오류 메시지
     * @param isProxyError 프록시 오류 여부
     * @return 실패한 PageResult 객체
     */
    public static PageResultDTO failure(int page, String error, boolean isProxyError) {
        return PageResultDTO.builder()
            .page(page)
            .success(false)
            .error(error)
            .isProxyError(isProxyError)
            .build();
    }
} 