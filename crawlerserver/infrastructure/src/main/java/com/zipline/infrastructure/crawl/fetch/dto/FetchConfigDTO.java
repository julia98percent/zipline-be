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
}
