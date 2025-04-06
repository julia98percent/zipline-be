package com.zipline.service.publicItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.publicItem.NaverRawArticle;
import com.zipline.entity.publicItem.Region;
import com.zipline.entity.publicItem.enums.CrawlStatus;
import com.zipline.entity.publicItem.enums.MigrationStatus;
import com.zipline.global.util.CoordinateUtil;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.repository.publicItem.NaverRawArticleRepository;
import com.zipline.repository.publicItem.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 네이버 부동산 API로부터 원본 매물 데이터를 수집하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NaverRawArticleService {
    private final ObjectMapper objectMapper;
    private final RegionRepository regionRepository;
    private final NaverRawArticleRepository naverRawArticleRepository;
    
    private static final String BASE_URL = "https://m.land.naver.com/cluster/ajax/articleList";
    private static final int RECENT_DAYS = 2; // 최근 2일
    private static final int ZOOM_LEVEL = 12; // 줌 레벨
    
    /**
     * 특정 레벨의 모든 지역에 대한 원본 매물 정보를 수집합니다.
     * 
     * @param level 지역 레벨
     */
    public void crawlAndSaveRawArticlesByLevel(int level) {
        log.info("=== 레벨 {} 네이버 원본 매물 정보 수집 시작 ===", level);
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
                
                Page<Long> regionPage = regionRepository.findRegionsNeedingUpdateForNaverWithPage(level, cutoffDate, pageRequest);
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
                        crawlAndSaveRawArticlesForRegion(cortarNo);
                        // 수집 완료 후 최종 수집 시간 업데이트
                        regionRepository.updateNaverLastCrawledAt(cortarNo, LocalDateTime.now());
                    } catch (Exception e) {
                        log.error("지역 코드 {} 처리 중 오류 발생: {}", cortarNo, e.getMessage());
                        // 필요시 재시도 로직 추가
                    }
                }
                // 다음 페이지로 이동
                pageNumber++;
                // 마지막 페이지인지 확인
                hasMoreData = pageNumber < regionPage.getTotalPages();
            }
            
            log.info("=== 레벨 {} 네이버 원본 매물 정보 수집 완료 ===", level);
        } catch (Exception e) {
            log.error("네이버 원본 매물 정보 수집 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 특정 지역의 원본 매물 정보를 수집하고 저장합니다.
     * 
     * @param cortarNo 지역 코드
     */
    @Transactional
    public void crawlAndSaveRawArticlesForRegion(Long cortarNo) {
        log.info("네이버 원본 매물 정보 수집 시작 - 지역 코드: {}", cortarNo);
        
        Region region = regionRepository.findByCortarNo(cortarNo)
                .orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다: " + cortarNo));
            
        // 상태 업데이트
        region.setNaverStatus(CrawlStatus.PROCESSING);
        region.setNaverLastCrawledAt(LocalDateTime.now());
        regionRepository.save(region);
        
        try {
            int page = 1;
            boolean hasMore = true;
            int totalArticles = 0;
            
            // 해당 지역의 기존 마이그레이션 상태를 초기화
            naverRawArticleRepository.resetMigrationStatusForRegion(cortarNo, MigrationStatus.PENDING);
            
            while (hasMore) {
                String apiUrl = buildApiUrl(cortarNo, page);
                log.info("API 요청 URL (페이지 {}): {}", page, apiUrl);
                
                String response = getArticles(apiUrl);
                if (response != null && !response.isEmpty()) {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode articlesNode = root.path("body");
                    
                    if (articlesNode.isArray()) {
                        for (JsonNode articleNode : articlesNode) {
                            saveRawArticle(articleNode, cortarNo);
                            totalArticles++;
                        }
                    }
                    
                    // 더 많은 데이터가 있는지 확인
                    hasMore = root.path("more").asBoolean();
                    log.info("페이지 {} 처리 완료. 더 많은 데이터: {}", page, hasMore);
                    
                    if (hasMore) {
                        RandomSleepUtil.sleep(); // 다음 페이지 요청 전 대기
                        page++;
                    }
                } else {
                    throw new RuntimeException("매물 목록이 비어있습니다.");
                }
            }
            
            // 성공 상태 업데이트
            region.setNaverStatus(CrawlStatus.COMPLETED);
            region.setNaverLastCrawledAt(LocalDateTime.now());
            regionRepository.save(region);
            
            log.info("네이버 원본 매물 정보 수집 완료 - 지역: {}, 총 {}개 매물", region.getCortarName(), totalArticles);
        } catch (Exception e) {
            log.error("네이버 원본 매물 정보 수집 중 오류 발생: {}", e.getMessage());
            region.setNaverStatus(CrawlStatus.FAILED);
            region.setNaverLastCrawledAt(LocalDateTime.now());
            regionRepository.save(region);
        }
    }
    
    /**
     * 네이버 부동산 API를 위한 URL을 생성합니다.
     * 
     * @param cortarNo 지역 코드
     * @param page 페이지 번호
     * @return API URL
     */
    private String buildApiUrl(Long cortarNo, int page) {
        Region region = regionRepository.findByCortarNo(cortarNo)
            .orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다: " + cortarNo));
        
        // 중심 좌표로부터 지리적 범위 계산
        double[] bounds = CoordinateUtil.calculateBounds(
            region.getCenterLat(),
            region.getCenterLon(),
            ZOOM_LEVEL
        );
        
        // [top, right, bottom, left] 순서로 반환됨
        double top = bounds[0];
        double right = bounds[1];
        double bottom = bounds[2];
        double left = bounds[3];
        
        log.info("지역: {}, 중심좌표: ({}, {}), 범위: top={}, right={}, bottom={}, left={}",
            region.getCortarName(),
            region.getCenterLat(),
            region.getCenterLon(),
            top,
            right,
            bottom,
            left
        );
        
        return String.format("%s?itemId=&mapKey=&lgeo=&showR0=&rletTpCd=APT:OPST:VL:YR:DSD:ABYG:OBYG:JGC:JWJT:DDDGG:SGJT:HOJT:JGB:OR:GSW:SG:SMS:GJCG:GM:TJ:APTHGJ&tradTpCd=A1:B1:B2:B3&z=%d&lat=%.6f&lon=%.6f&btm=%.6f&lft=%.6f&top=%.6f&rgt=%.6f&cortarNo=%d&sort=rank&page=%d",
            BASE_URL,
            ZOOM_LEVEL,
            region.getCenterLat(),
            region.getCenterLon(),
            bottom,
            left,
            top,
            right,
            cortarNo,
            page
        );
    }
    
    /**
     * 원본 매물 정보를 데이터베이스에 저장합니다.
     * 
     * @param articleNode 매물 정보 JSON 노드
     * @param cortarNo 지역 코드
     */
    @Transactional
    private void saveRawArticle(JsonNode articleNode, Long cortarNo) {
        try {
            String articleId = articleNode.path("atclNo").asText();
            Optional<NaverRawArticle> existingArticle = naverRawArticleRepository.findByArticleId(articleId);
            
            NaverRawArticle rawArticle;
            if (existingArticle.isPresent()) {
                rawArticle = existingArticle.get();
                log.info("기존 원본 매물 정보 업데이트: {}", articleId);
                // 데이터가 업데이트되면 마이그레이션 상태를 초기화합니다
                rawArticle.setMigrationStatus(MigrationStatus.PENDING);
            } else {
                rawArticle = new NaverRawArticle();
                rawArticle.setArticleId(articleId);
                rawArticle.setCortarNo(cortarNo);
                rawArticle.setMigrationStatus(MigrationStatus.PENDING);
                log.info("새로운 원본 매물 정보 생성: {}", articleId);
            }
            
            // 전체 JSON 데이터를 문자열로 저장
            rawArticle.setRawData(articleNode.toString());
            
            naverRawArticleRepository.save(rawArticle);
            log.info("원본 매물 정보 저장 완료: {}", articleId);
        } catch (Exception e) {
            log.error("원본 매물 정보 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("원본 매물 정보 저장 실패", e);
        }
    }
    
    /**
     * 네이버 부동산 API에서 매물 정보를 가져옵니다.
     * 
     * @param apiUrl API URL
     * @return API 응답 문자열
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
            log.error("API 요청 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}
