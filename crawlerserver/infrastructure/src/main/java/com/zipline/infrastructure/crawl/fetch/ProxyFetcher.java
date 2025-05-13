package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import com.zipline.infrastructure.proxy.ProxyPool;
import com.zipline.infrastructure.proxy.dto.ProxyInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
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
        return executeGet(url, config);
    }

    @Override
    public String fetchPost(String url, String jsonBody, FetchConfigDTO config) throws Exception {
        return executePost(url, jsonBody, config);
    }


    //todo:프록시 사용검증 필요
    private String executeGet(String url, FetchConfigDTO config) throws Exception {
        ProxyInfoDTO proxy = proxyPool.getNextAvailableProxy();
        Proxy javaProxy = new Proxy(java.net.Proxy.Type.HTTP,
                new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config, javaProxy);


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
            log.warn("HTTP 오류 코드: {}, 재시도 중...)", responseCode);
            proxyPool.markProxyAsFailed(proxy);
            return executeGet(url, config);
        }
    }

    private String executePost(String url, String jsonBody, FetchConfigDTO config) throws Exception {
        ProxyInfoDTO proxy = proxyPool.getNextAvailableProxy();

        Proxy javaProxy = new Proxy(java.net.Proxy.Type.HTTP,
                new InetSocketAddress(proxy.getHost(), proxy.getPort()));

        HttpURLConnection conn = Connection.HTTPURLConnection(url, config, javaProxy);

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
            log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {})", responseCode, MAX_RETRY_COUNT);
            proxyPool.markProxyAsFailed(proxy);
            return executePost(url, jsonBody, config);
        }
    }
}