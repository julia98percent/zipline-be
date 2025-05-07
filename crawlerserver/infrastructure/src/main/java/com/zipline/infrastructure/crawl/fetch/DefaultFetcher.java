package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class DefaultFetcher implements Fetcher {
        @Override
        public String fetch(String url, FetchConfigDTO config) throws Exception {
            HttpURLConnection conn = Connection.HTTPURLConnection(url, config);

            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    return sb.toString();
                }
            } else {
                throw new RuntimeException("HTTP Error: " + conn.getResponseCode());
            }
        }

    }
