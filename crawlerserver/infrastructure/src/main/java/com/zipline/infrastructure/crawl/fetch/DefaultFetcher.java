package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component("defaultFetcher")
@RequiredArgsConstructor
public class DefaultFetcher implements Fetcher {

    private static final int MAX_RETRY_COUNT = 5;

    @Override
    public String fetch(String url, FetchConfigDTO config) throws Exception {
        return executeGet(url, config, 0);
    }

    @Override
    public String fetchPost(String url, String jsonBody, FetchConfigDTO config) throws Exception {
        return executePost(url, jsonBody, config, 0);
    }

    private String executeGet(String url, FetchConfigDTO config, int retryCount) throws Exception {
        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);

        int responseCode = conn.getResponseCode();
        String responseMsg = conn.getResponseMessage();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        }else if(responseCode == 400){
            log.warn("400 Bad Request - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "400";
        }else if (responseCode == 307) {
            log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "307";
        } else {
            if (retryCount < MAX_RETRY_COUNT) {
                log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {}) 요청url:{} 요청응답:[{}]{}", responseCode, retryCount + 1, MAX_RETRY_COUNT, url, responseCode, responseMsg);
                Thread.sleep((long) (Math.pow(2, retryCount) * 1000));
                return executeGet(url, config, retryCount + 1);
            } else {
                throw new RuntimeException("GET 요청 실패: " + url + ", 응답 코드: " + responseCode);
            }
        }
    }

    private String executePost(String url, String jsonBody, FetchConfigDTO config, int retryCount) throws Exception {
        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        String responseMsg = conn.getResponseMessage();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        }else if(responseCode == 400){
            log.warn("400 Bad Request - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "400";
        }else if (responseCode == 307) {
            log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "307";
        } else {
            if (retryCount < MAX_RETRY_COUNT) {
                log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {})", responseCode, retryCount + 1, MAX_RETRY_COUNT);
                Thread.sleep((long) (Math.pow(2, retryCount) * 1000));
                return executePost(url, jsonBody, config, retryCount + 1);
            } else {
                throw new RuntimeException("POST 요청 실패: " + url + ", 응답 코드: " + responseCode);
            }
        }
    }
}