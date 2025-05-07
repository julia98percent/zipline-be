package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {
    protected static HttpURLConnection HTTPURLConnection(String url, FetchConfigDTO config) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(config.getConnectTimeout());
        conn.setReadTimeout(config.getReadTimeout());
        conn.setRequestProperty("Referer", config.getReferer());
        conn.setRequestProperty("User-Agent", config.getUserAgent());
        conn.setRequestProperty("Accept", config.getAccept());
        conn.setRequestProperty("Accept-Language", config.getAcceptLanguage());
        return conn;
    }
}
