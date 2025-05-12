package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {
    protected static HttpURLConnection HTTPURLConnection(String url, FetchConfigDTO config) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(config.getRequestMethod());
        conn.setDoOutput(config.isDoOutput());
        conn.setConnectTimeout(config.getConnectTimeout());
        conn.setReadTimeout(config.getReadTimeout());
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

        String requestProperty = config.getRequestProperty();
        if (requestProperty != null && requestProperty.equals("XZP")) {
            conn.setRequestProperty("X-Zigbang-Platform", "www");
        }

        return conn;
    }
}
