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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component("proxyFetcher")
@RequiredArgsConstructor
public class ProxyFetcher implements Fetcher {

    private final ProxyPool proxyPool;
    private static final int MAX_RETRY_COUNT = 10;
    private static final long RETRY_DELAY_MS = 3000;

    @Override
    public String fetch(String url, FetchConfigDTO config) throws Exception {
        return executeGet(url, config, 0);
    }

    @Override
    public String fetchPost(String url, String jsonBody, FetchConfigDTO config) throws Exception {
        return executePostWithRetry(url, jsonBody, config, 0);
    }

    private String executeGet(String url, FetchConfigDTO config, int retryCount) throws Exception {
        ProxyInfoDTO proxy = getAvailableProxy(retryCount);
        java.net.Proxy javaProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } else if (responseCode == 307) {
            log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: {}", url);
            return null;
        } else {
            log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {})", responseCode, retryCount + 1, MAX_RETRY_COUNT);
            proxyPool.markProxyAsFailed(proxy);
            retryCount++;
            sleepWithBackoff(retryCount);
            return executeGet(url, config, retryCount);
        }
    }

    private String executePostWithRetry(String url, String jsonBody, FetchConfigDTO config, int retryCount) throws Exception {
        ProxyInfoDTO proxy = getAvailableProxy(retryCount);

        java.net.Proxy javaProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } else if (responseCode == 307) {
            log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: {}", url);
            return null;
        } else {
            log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {})", responseCode, retryCount + 1, MAX_RETRY_COUNT);
            proxyPool.markProxyAsFailed(proxy);
            retryCount++;
            sleepWithBackoff(retryCount);
            return executePostWithRetry(url, jsonBody, config, retryCount);
        }
    }

    //TODO: 이미 프록시풀에 구현된 검증을 중복검증하는지 확인 필요
    private ProxyInfoDTO getAvailableProxy(int retryCount) throws Exception {
        ProxyInfoDTO proxy = proxyPool.getNextAvailableProxy();
        if (proxy == null && retryCount == 0) {
            proxyPool.refreshProxyPool();
            proxy = proxyPool.getNextAvailableProxy();
        }
        if (proxy == null) throw new RuntimeException("사용 가능한 프록시 없음.");
        return proxy;
    }

    private void sleepWithBackoff(int retryCount) {
        try {
            Thread.sleep(RETRY_DELAY_MS * retryCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}