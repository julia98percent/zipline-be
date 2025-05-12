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
    private boolean doOutput;
    private String accept;
    private String contentType;
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
    private String requestProperty;
    private int connectTimeout;
    private int readTimeout;

    public static FetchConfigDTO naverDefaultConfig() {
        return FetchConfigDTO.builder()
                .requestMethod("GET")
                .doOutput(false)
                .accept("application/json")
                .host("m.land.naver.com")
                .referer("https://m.land.naver.com/")
                .secChUa("\"Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?1")
                .secChUaPlatform("\"Android\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("same-origin")
                .userAgent("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();
    }

    public static FetchConfigDTO zigbangDefaultConfig() {
        return FetchConfigDTO.builder()
                .requestMethod("GET")
                .doOutput(false)
                .accept("application/json")
                .contentType("application/json; charset=UTF-8")
                .host("https://www.zigbang.com/")
                .referer("https://www.zigbang.com/")
                .secChUa("\"Not A(Brand\";v=\"99\", \"Chromium\";v=\"122\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?0")
                .secChUaPlatform("\"Windows\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("cross-site")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .connectTimeout(10000)
                .readTimeout(15000)
                .build();
    }

    public static FetchConfigDTO zigbangPostConfig() {
        return FetchConfigDTO.builder()
                .requestMethod("POST")
                .doOutput(true)
                .accept("application/json")
                .host("apis.zigbang.com")
                .referer("https://www.zigbang.com/")
                .secChUa("\"Not A(Brand\";v=\"99\", \"Chromium\";v=\"122\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?0")
                .secChUaPlatform("\"Windows\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("same-site")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .requestProperty("XZP")
                .connectTimeout(10000)
                .readTimeout(15000)
                .build();
    }
}
