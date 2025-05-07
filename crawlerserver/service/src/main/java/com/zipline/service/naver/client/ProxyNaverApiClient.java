package com.zipline.service.naver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.infrastructure.proxy.ProxyPool;
import com.zipline.infrastructure.proxy.dto.ProxyInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyNaverApiClient {

    private final ProxyPool proxyPool;
    private final ObjectMapper objectMapper;

    @Value("${crawler.max-retry-count:10}")
    private int maxRetryCount;

    @Value("${crawler.retry-delay-ms:1000}")
    private long retryDelayMs;

    public String fetchArticleList(String apiUrl) {
        ProxyInfoDTO proxy = null;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetryCount) {
            try {
                proxy = proxyPool.getNextAvailableProxy();
                if (proxy == null) {
                    proxyPool.refreshProxyPool();
                    proxy = proxyPool.getNextAvailableProxy();
                }

                if (proxy == null) {
                    throw new RuntimeException("프록시 풀 비어 있음.");
                }

                log.info("API 요청: {}, 프록시: {}", apiUrl, proxy.getKey());

                URL url = new URL(apiUrl);
                java.net.Proxy javaProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxy.getHost(), proxy.getPort()));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection(javaProxy);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Referer", "https://m.land.naver.com/");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                int code = conn.getResponseCode();
                if (code == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        return sb.toString();
                    }
                } else {
                    log.warn("HTTP 오류 코드: {}, 재시도 중...", code);
                    proxyPool.markProxyAsFailed(proxy);
                    retryCount++;
                }

            } catch (Exception e) {
                lastException = e;
                log.error("프록시 요청 실패: {}", e.getMessage());
                if (proxy != null) proxyPool.markProxyAsFailed(proxy);
                retryCount++;
                sleepWithBackoff(retryCount);
            }
        }

        log.error("모든 재시도 실패: {}", lastException.getMessage());
        return null;
    }

    private void sleepWithBackoff(int retryCount) {
        try {
            Thread.sleep(retryDelayMs * retryCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}