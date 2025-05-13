package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

public class Connection {

    // 프록시 없는 요청
    protected static HttpURLConnection HTTPURLConnection(String url, FetchConfigDTO config) throws IOException {
        return HTTPURLConnection(url, config, null);
    }

    protected static HttpURLConnection HTTPURLConnection(String url, FetchConfigDTO config, Proxy proxy) throws IOException {
        HttpURLConnection conn;
        if (proxy != null) {
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);
        } else {
            conn = (HttpURLConnection) new URL(url).openConnection();
        }

        // 연결 설정
        conn.setRequestMethod(config.getRequestMethod());
        conn.setDoOutput(config.isDoOutput());
        conn.setConnectTimeout(config.getConnectTimeout());
        conn.setReadTimeout(config.getReadTimeout());

        // 리퀘스트 헤더 설정
        conn.setRequestProperty("Referer", config.getReferer());
        conn.setRequestProperty("User-Agent", config.getUserAgent());
        conn.setRequestProperty("Accept", config.getAccept());
        conn.setRequestProperty("Accept-Language", config.getAcceptLanguage());
        conn.setRequestProperty("Host", config.getHost());
        conn.setRequestProperty("Accept", config.getAccept());
        conn.setRequestProperty("Accept-Language", config.getAcceptLanguage());
        conn.setRequestProperty("Sec-Ch-Ua", config.getSecChUa());
        conn.setRequestProperty("Sec-Ch-Ua-Mobile", config.getSecChUaMobile());
        conn.setRequestProperty("Sec-Ch-Ua-Platform", config.getSecChUaPlatform());
        conn.setRequestProperty("Sec-Fetch-Dest", config.getSecFetchDest());
        conn.setRequestProperty("Sec-Fetch-Mode", config.getSecFetchMode());
        conn.setRequestProperty("Sec-Fetch-Site", config.getSecFetchSite());

        //추가 옵션 처리
        if (config.getExtraHeader() != null) {
            for (Map.Entry<String, String> entry : config.getExtraHeader().entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }
}
