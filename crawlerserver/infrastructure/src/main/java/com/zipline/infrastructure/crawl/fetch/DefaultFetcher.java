package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component("defaultFetcher")
public class DefaultFetcher extends AbstractFetcher {

    @Override
    protected String executeGet(String url, FetchConfigDTO config, int retryCount) throws Exception {
        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);
        return handleResponse(conn, url, retryCount);
    }

    @Override
    protected String executePost(String url, String jsonBody, FetchConfigDTO config, int retryCount) throws Exception {
        HttpURLConnection conn = Connection.HTTPURLConnection(url, config);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return handleResponse(conn, url, retryCount);
    }

    @Override
    protected String handleErrorResponse(String url, int responseCode, String responseMsg, int retryCount) throws Exception {
        if (retryCount < MAX_RETRY_COUNT) {
            log.warn("HTTP 오류 코드: {}, 재시도 중... ({} / {}) 요청url:{} 요청응답:[{}]{}",
                    responseCode, retryCount + 1, MAX_RETRY_COUNT, url, responseCode, responseMsg);
            Thread.sleep((long) (Math.pow(2, retryCount) * 1000));

            // 요청 타입에 따라 적절한 메서드 재실행
            if (url.contains("GET")) {
                return executeGet(url, null, retryCount + 1);
            } else {
                return executePost(url, null, null, retryCount + 1);
            }
        } else {
            throw new RuntimeException("요청 실패: " + url + ", 응답 코드: " + responseCode + ", 응답 메시지: " + responseMsg);
        }
    }
}