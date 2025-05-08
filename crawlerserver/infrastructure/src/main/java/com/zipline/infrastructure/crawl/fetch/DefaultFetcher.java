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

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    return sb.toString();
                }
            }else if(responseCode == 307){
                log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: {}",url);
                return null;
            } else {
                throw new RuntimeException("HTTP Error: " + conn.getResponseCode());
            }
        }

    }
