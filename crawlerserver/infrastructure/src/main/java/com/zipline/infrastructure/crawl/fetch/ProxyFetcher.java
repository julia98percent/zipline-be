package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import com.zipline.infrastructure.proxy.ProxyPool;
import com.zipline.infrastructure.proxy.dto.ProxyInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component("proxyFetcher")
@RequiredArgsConstructor
public class ProxyFetcher extends AbstractFetcher {

    private final ProxyPool proxyPool;

    @Override
    protected String executeGet(String url, FetchConfigDTO config, int retryCount) throws Exception {
        if (retryCount >= MAX_RETRY_COUNT) {
            throw new RuntimeException("GET 요청 실패 (최대 재시도 횟수 초과): " + url);
        }

        ProxyInfoDTO proxy = proxyPool.getNextAvailableProxy();
        Proxy javaProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config, javaProxy);
        try {
            return handleResponse(conn, url, retryCount);
        } catch (Exception e) {
            proxyPool.markProxyAsFailed(proxy);
            log.warn("프록시 오류 발생, 다른 프록시로 재시도 ({}/{}): {}", retryCount + 1, MAX_RETRY_COUNT, e.getMessage());
            return executeGet(url, config, retryCount + 1);
        }
    }

    @Override
    protected String executePost(String url, String jsonBody, FetchConfigDTO config, int retryCount) throws Exception {
        if (retryCount >= MAX_RETRY_COUNT) {
            throw new RuntimeException("POST 요청 실패 (최대 재시도 횟수 초과): " + url);
        }

        ProxyInfoDTO proxy = proxyPool.getNextAvailableProxy();
        Proxy javaProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config, javaProxy);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try {
            return handleResponse(conn, url, retryCount);
        } catch (Exception e) {
            proxyPool.markProxyAsFailed(proxy);
            log.warn("프록시 오류 발생, 다른 프록시로 재시도 ({}/{}): {}", retryCount + 1, MAX_RETRY_COUNT, e.getMessage());
            return executePost(url, jsonBody, config, retryCount + 1);
        }
    }

    @Override
    protected String handleErrorResponse(String url, int responseCode, String responseMsg, int retryCount) throws Exception {
        if (retryCount >= MAX_RETRY_COUNT) {
            throw new RuntimeException("요청 실패 (최대 재시도 횟수 초과): " + url + ", 응답 코드: " + responseCode + ", 응답 메시지: " + responseMsg);
        }

        log.warn("HTTP 오류 코드: {}, 프록시 변경 후 재시도 중... ({}/{}) 요청url:{}",
                responseCode, retryCount + 1, MAX_RETRY_COUNT, url);

        ProxyInfoDTO currentProxy = proxyPool.getCurrentProxy();
        if (currentProxy != null) {
            proxyPool.markProxyAsFailed(currentProxy);
        }

        Thread.sleep((long) (Math.pow(2, retryCount) * 1000));

        if (url.contains("GET")) {
            return executeGet(url, null, retryCount + 1);
        } else {
            return executePost(url, null, null, retryCount + 1);
        }
    }
}