package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Slf4j
public abstract class AbstractFetcher implements Fetcher {

    protected static final int MAX_RETRY_COUNT = 5;

    @Override
    public String fetch(String url, FetchConfigDTO config) throws Exception {
        return executeGet(url, config, 0);
    }

    @Override
    public String fetchPost(String url, String jsonBody, FetchConfigDTO config) throws Exception {
        return executePost(url, jsonBody, config, 0);
    }

    protected abstract String executeGet(String url, FetchConfigDTO config, int retryCount) throws Exception;

    protected abstract String executePost(String url, String jsonBody, FetchConfigDTO config, int retryCount) throws Exception;

    /**
     * HTTP 응답 처리를 위한 공통 메서드
     */
    protected String handleResponse(HttpURLConnection conn, String url, int retryCount) throws Exception {
        int responseCode = conn.getResponseCode();
        String responseMsg = conn.getResponseMessage();

        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                String response = sb.toString();
                log.info("요청 성공 - 데이터 조회 완료 - url: {}, 응답 코드: {}, 응답 값: {}", url, responseCode, response);
                return response;
            }
        } else if (responseCode == 400) {
            log.warn("400 Bad Request - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "400";
        } else if (responseCode == 307) {
            log.warn("307 리다이렉트 발생 - 데이터 없음으로 간주하고 스킵: [{}]{}", url, responseMsg);
            return "307";
        } else {
            return handleErrorResponse(url, responseCode, responseMsg, retryCount);
        }
    }

    protected abstract String handleErrorResponse(String url, int responseCode, String responseMsg, int retryCount) throws Exception;
}
