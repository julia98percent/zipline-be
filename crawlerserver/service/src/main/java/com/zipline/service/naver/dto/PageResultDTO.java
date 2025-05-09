package com.zipline.service.naver.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class
PageResultDTO {
    private int page;
    private boolean hasMore;
    private boolean success;
    private boolean proxyError;
    private String error;
    private List<JsonNode> articles;
    
    public static PageResultDTO success(int page, List<JsonNode> articles, boolean hasMore) {
        return PageResultDTO.builder()
            .page(page)
            .articles(articles)
            .hasMore(hasMore)
            .success(true)
            .proxyError(false)
            .build();
    }

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

    public boolean isSuccess() {
        return success;
    }
    
    public boolean isHasMore() {
        return hasMore;
    }

    public boolean isProxyError() {
        return proxyError;
    }

    public String getError() {
        return error;
    }

    public List<JsonNode> getArticles() {
        return articles;
    }
} 
