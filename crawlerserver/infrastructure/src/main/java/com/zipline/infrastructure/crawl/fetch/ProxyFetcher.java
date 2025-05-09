package com.zipline.infrastructure.crawl.fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import com.zipline.infrastructure.proxy.ProxyPool;
import com.zipline.infrastructure.proxy.dto.ProxyInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyFetcher implements Fetcher {

    private final ProxyPool proxyPool;

    @Value("${crawler.max-retry-count:10}")
    private int maxRetryCount;

    @Value("${crawler.retry-delay-ms:1000}")
    private long retryDelayMs;

    @Override
    public String fetch(String url, FetchConfigDTO config) throws Exception {
        ProxyInfoDTO proxy = null;
        int retryCount = 0;

        while (retryCount < maxRetryCount) {
            try {
                proxy = proxyPool.getNextAvailableProxy();
                if (proxy == null) {
                    proxyPool.refreshProxyPool();
                    proxy = proxyPool.getNextAvailableProxy();
                }
                if (proxy == null) throw new RuntimeException("프록시 풀 비어 있음.");
                log.info("API 요청: {}, 프록시: {}", url, proxy.getKey());
                java.net.Proxy javaProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                        new InetSocketAddress(proxy.getHost(), proxy.getPort()));

                HttpURLConnection conn = Connection.HTTPURLConnection(url, config);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) sb.append(line);
                        return sb.toString();
                    }
                }else if(responseCode == 307){
                    log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: {}",url);
                    return null;
                } else {
                    log.warn("HTTP 오류 코드: {}, 재시도 중...", conn.getResponseCode());
                    proxyPool.markProxyAsFailed(proxy);
                    retryCount++;
                }
            } catch (Exception e) {
                log.error("프록시 요청 실패: {}", e.getMessage());
                if (proxy != null) proxyPool.markProxyAsFailed(proxy);
                retryCount++;
                sleepWithBackoff(retryCount);
            }
        }
        throw new RuntimeException("모든 재시도 실패");
    }
    private void sleepWithBackoff(int retryCount) {
        try {
            Thread.sleep(retryDelayMs * retryCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}