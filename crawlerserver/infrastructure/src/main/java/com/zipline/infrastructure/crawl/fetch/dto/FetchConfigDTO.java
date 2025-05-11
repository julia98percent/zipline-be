package com.zipline.infrastructure.crawl.fetch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FetchConfigDTO {
    private String requestMethod;
    private String accept;
    private String host;
    private String referer;
    private String secChUa;
    private String secChUaMobile;
    private String secChUaPlatform;
    private String secFetchDest;
    private String secFetchMode;
    private String secFetchSite;
    private String userAgent;
    private String acceptLanguage;
    private int connectTimeout;
    private int readTimeout;

    public static FetchConfigDTO zigbangDefaultConfig() {
        return FetchConfigDTO.builder()
                .requestMethod("GET")
                .accept("application/json")
                .host("api.zigbang.com")
                .referer("https://www.zigbang.com/ ")
                .secChUa("\"Not A(Brand\";v=\"99\", \"Chromium\";v=\"122\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?0")
                .secChUaPlatform("\"Windows\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("cross-site")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();
    }

    public static FetchConfigDTO zigbangPostConfig() {
        return FetchConfigDTO.builder()
                .requestMethod("POST")
                .accept("application/json")
                .host("api.zigbang.com")
                .referer("https://www.zigbang.com/ ")
                .secChUa("\"Not A(Brand\";v=\"99\", \"Chromium\";v=\"122\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?0")
                .secChUaPlatform("\"Windows\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("cross-site")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();
    }
}
