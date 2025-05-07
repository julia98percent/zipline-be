package com.zipline.infrastructure.crawl.fetch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FetchConfigDTO {
    private String referer;
    private String userAgent;
    private String accept;
    private String acceptLanguage;
    private int connectTimeout;
    private int readTimeout;
}
