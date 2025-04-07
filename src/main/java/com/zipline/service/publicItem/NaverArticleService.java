package com.zipline.service.publicItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.enums.CrawlStatus;
import com.zipline.entity.publicItem.PropertyArticle;
import com.zipline.entity.publicItem.Region;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.repository.publicItem.PropertyArticleRepository;
import com.zipline.repository.publicItem.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverArticleService {
    private final ObjectMapper objectMapper;
    private final RegionRepository regionRepository;
    private final PropertyArticleRepository propertyArticleRepository;
    
    private static final String BASE_URL = "https://m.land.naver.com/cluster/ajax/articleList";
    private static final int RECENT_DAYS = 2; // 최근 2일
    private static final int ZOOM_LEVEL = 12; // 줌 레벨

    /**
     * 특정 레벨의 모든 지역에 대한 매물 정보를 수집합니다.
     */
    public void crawlAndSaveArticlesByLevel(int level) {
        log.info("=== 레벨 {} 매물 정보 수집 시작 ===", level);
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RECENT_DAYS);
            log.info("수집 기준일: {}", cutoffDate);

            // 페이징 처리를 위한 변수들
            int pageSize = 1;
            int pageNumber = 0;
            boolean hasMoreData = true;

            while (hasMoreData) {
                PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
                log.info("페이지 요청: {}", pageRequest);
                
                Page<Long> regionPage = regionRepository.findRegionsNeedingUpdateForNaverWithPage(
                    level, cutoffDate, pageRequest);
                log.info("조회된 지역 수: {}", regionPage.getContent().size());
                
                if (regionPage.isEmpty()) {
                    log.info("더 이상 처리할 지역이 없습니다.");
                    hasMoreData = false;
                    continue;
                }
                
                List<Long> cortarNos = regionPage.getContent();
                log.info("배치 처리 중: 페이지 {}, 지역 수: {}", pageNumber + 1, cortarNos.size());
                
                // 각 지역에 대해 매물 정보 수집
                for (Long cortarNo : cortarNos) {
                    try {
                        RandomSleepUtil.sleep(); // API 요청 전 대기
                        crawlAndSaveArticlesForRegion(cortarNo);
                        
                        // 수집 완료 후 최종 수집 시간 업데이트
                        regionRepository.updateNaverLastCrawledAt(cortarNo, LocalDateTime.now());
                        log.info("지역 코드 {} 최종 수집 시간 업데이트 완료", cortarNo);
                    } catch (Exception e) {
                        log.error("지역 코드 {} 처리 중 오류 발생: {}", cortarNo, e.getMessage(), e);
                        // 필요시 재시도 로직 추가
                    }
                }
                
                // 다음 페이지로 이동
                pageNumber++;
                
                // 마지막 페이지인지 확인
                hasMoreData = pageNumber < regionPage.getTotalPages();
            }
            
            log.info("=== 레벨 {} 매물 정보 수집 완료 ===", level);
        } catch (Exception e) {
            log.error("매물 정보 수집 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 지역의 매물 정보를 수집하고 저장합니다.
     */
    public void crawlAndSaveArticlesForRegion(Long cortarNo) {
        log.info("매물 정보 수집 시작 - 지역 코드: {}", cortarNo);
        
        Region region = regionRepository.findByCortarNo(cortarNo)
                .orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다: " + cortarNo));
            
        // 상태 업데이트 - 직접 리포지토리 메서드 사용
        regionRepository.updateNaverStatus(cortarNo, CrawlStatus.PROCESSING);
        log.info("지역 코드 {} 상태 업데이트: {}", cortarNo, CrawlStatus.PROCESSING);
        
        try {
            int page = 1;
            boolean hasMore = true;
            int totalArticles = 0;
            
            while (hasMore) {
                String apiUrl = buildApiUrl(cortarNo, page);
                log.info("API 요청 URL (페이지 {}): {}", page, apiUrl);
                String response = getArticles(apiUrl);
                if (response != null && !response.isEmpty()) {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode articlesNode = root.path("body");
                        if (articlesNode.isArray()) {
                            for (JsonNode articleNode : articlesNode) {
                                saveArticle(articleNode, region);
                                totalArticles++;
                            }
                        }
                        // 더 많은 데이터가 있는지 확인
                        hasMore = root.path("more").asBoolean();
                        log.info("페이지 {} 처리 완료. 더 많은 데이터: {}", page, hasMore);
                    } catch (Exception e) {
                        log.error("JSON 파싱 중 오류 발생: {}", e.getMessage(), e);
                        // 오류 발생 시 다음 페이지로 넘어가기
                        hasMore = false;
                    }
                } else {
                    log.error("API 응답이 비어있습니다.");
                    hasMore = false;
                }
                page++;
            }
            
            // 성공 상태 업데이트 - 직접 리포지토리 메서드 사용
            regionRepository.updateNaverStatus(cortarNo, CrawlStatus.COMPLETED);
            log.info("지역 코드 {} 상태 업데이트: {}", cortarNo, CrawlStatus.COMPLETED);
            log.info("매물 정보 수집 완료 - 지역: {}, 총 {}개 매물", region.getCortarName(), totalArticles);
        } catch (Exception e) {
            log.error("매물 정보 수집 중 오류 발생: {}", e.getMessage(), e);
            // 실패 상태 업데이트 - 직접 리포지토리 메서드 사용
            regionRepository.updateNaverStatus(cortarNo, CrawlStatus.FAILED);
            log.info("지역 코드 {} 상태 업데이트: {}", cortarNo, CrawlStatus.FAILED);
        }
    }

    /**
     * 매물 정보를 저장합니다.
     */
    private void saveArticle(JsonNode articleNode, Region region) {
        try {
            String articleId = articleNode.path("atclNo").asText();
            
            // 기존 매물 확인
            Optional<PropertyArticle> existingArticle = propertyArticleRepository.findByArticleId(articleId);
            
            if (existingArticle.isPresent()) {
                // 기존 매물 업데이트 로직 (필요시 구현)
                log.debug("기존 매물 발견: {}", articleId);
            } else {
                // 새 매물 생성 (빌더 패턴 사용)
                PropertyArticle article = PropertyArticle.createFromNaverArticle(articleNode, region);
                propertyArticleRepository.save(article);
                log.debug("새 매물 저장: {}", articleId);
            }
        } catch (Exception e) {
            log.error("매물 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * API URL을 생성합니다.
     */
    private String buildApiUrl(Long cortarNo, int page) {
        return BASE_URL + "?cortarNo=" + cortarNo + "&page=" + page + "&zoom=" + ZOOM_LEVEL;
    }

    /**
     * 네이버 부동산 API에서 매물 정보를 가져옵니다.
     */
    private String getArticles(String apiUrl) {
        try {
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
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
            
            int responseCode = conn.getResponseCode();
            log.info("응답 코드: {}", responseCode);
            
            if (responseCode == 200) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                log.error("API 요청 실패. 응답 코드: {}", responseCode);
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    log.error("에러 응답: {}", errorResponse.toString());
                }
                return null;
            }
        } catch (Exception e) {
            log.error("API 요청 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
