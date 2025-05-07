package com.zipline.service.naver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverApiClient {

    private final ObjectMapper objectMapper;

    /**
     * 특정 지역의 매물 목록을 API에서 가져옴
     */
    public String fetchArticleList(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            setRequestHeaders(conn);

            int responseCode = conn.getResponseCode();
            log.info("응답 코드: {}", responseCode);

            if (responseCode == 200) {
                return readResponse(conn.getInputStream());
            } else {
                log.error("API 요청 실패. 응답 코드: {}", responseCode);
                String errorResponse = readResponse(conn.getErrorStream());
                log.error("에러 응답: {}", errorResponse);
                return null;
            }
        } catch (IOException e) {
            log.error("API 요청 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    private void setRequestHeaders(HttpURLConnection conn) {
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Host", "m.land.naver.com");
        conn.setRequestProperty("Referer", "https://m.land.naver.com/");
        conn.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122\"");
        conn.setRequestProperty("sec-ch-ua-mobile", "?1");
        conn.setRequestProperty("sec-ch-ua-platform", "\"Android\"");
        conn.setRequestProperty("Sec-Fetch-Dest", "empty");
        conn.setRequestProperty("Sec-Fetch-Mode", "cors");
        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
    }

    private String readResponse(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}