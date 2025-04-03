package com.zipline.service.publicItem;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.dto.publicItem.CrawlingStatusDTO;
import com.zipline.dto.publicItem.PageResultDTO;
import com.zipline.dto.publicItem.ProxyInfoDTO;
import com.zipline.entity.PropertyArticle;
import com.zipline.entity.publicItem.Region;
import com.zipline.global.util.ProxyPool;
import com.zipline.repository.publicItem.PropertyArticleRepository;
import com.zipline.repository.publicItem.RegionRepository;
import com.zipline.global.util.CoordinateUtil;
import com.zipline.global.util.RandomSleepUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

/**
 * 네이버 부동산 매물 정보를 프록시를 통해 수집하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyNaverArticleService {

    private final RegionRepository regionRepository;
    private final PropertyArticleRepository propertyArticleRepository;
    private final ObjectMapper objectMapper;
    @Getter
    private final ProxyPool proxyPool;
    
    private static final int MAX_CONCURRENT_REQUESTS = 20;
    private static final String BASE_URL = "https://m.land.naver.com/cluster/ajax/articleList";
    private static final String REFERER = "https://new.land.naver.com";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;
    private static final int ZOOM_LEVEL = 12;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_REQUESTS);
    private final ConcurrentHashMap<String, CrawlingStatusDTO> regionStatuses = new ConcurrentHashMap<>();

    @Value("${crawler.max-retry-count:10}")
    private int maxRetryCount;

    @Value("${crawler.retry-delay-ms:1000}")
    private long retryDelayMs;

    public void crawlAndSaveArticlesByLevel(int level) {
        log.info("[Thread-{}] === 프록시를 통한 레벨 {} 매물 정보 수집 시작 ===", Thread.currentThread().getId(), level);
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // 일주일 전
            log.info("[Thread-{}] 수집 기준일: {}", Thread.currentThread().getId(), cutoffDate);
            
            List<Region> regionsToUpdate = regionRepository.findRegionsNeedingUpdateForNaver(level, cutoffDate);
            log.info("[Thread-{}] 처리할 총 지역 수: {}", Thread.currentThread().getId(), regionsToUpdate.size());
            
            // 지역 간 병렬 처리를 위한 CompletableFuture 리스트
            List<CompletableFuture<Void>> regionFutures = new ArrayList<>();
            
            // 지역별 병렬 처리 시작
            for (Region region : regionsToUpdate) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        crawlAndSaveArticlesForRegion(region);
                    } catch (Exception e) {
                        log.error("[Thread-{}] [{}] 지역 처리 중 오류 발생: {}", Thread.currentThread().getId(), region.getCortarName(), e.getMessage(), e);
                    }
                }, executorService);
                
                regionFutures.add(future);
                
                // 최대 동시 처리 지역 수 제한 (프록시 풀 크기에 따라 조정)
                if (regionFutures.size() >= MAX_CONCURRENT_REQUESTS) {
                    // 일부 지역 처리가 완료될 때까지 대기
                    CompletableFuture.anyOf(regionFutures.toArray(new CompletableFuture[0])).join();
                    // 완료된 지역 제거
                    regionFutures.removeIf(CompletableFuture::isDone);
                }
                
                // 지역 간 짧은 대기 시간 추가
                RandomSleepUtil.sleepShort();
            }
            
            // 모든 지역 처리가 완료될 때까지 대기
            CompletableFuture.allOf(regionFutures.toArray(new CompletableFuture[0])).join();
            
            log.info("[Thread-{}] === 프록시를 통한 레벨 {} 매물 정보 수집 완료 ===", Thread.currentThread().getId(), level);
        } catch (Exception e) {
            log.error("[Thread-{}] 매물 정보 수집 중 오류 발생: {}", Thread.currentThread().getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 지역의 매물 정보를 수집하고 저장합니다.
     * 
     * @param cortarNo 지역 코드
     */
    public void crawlAndSaveArticlesForRegion(Long cortarNo) {
        Region region = regionRepository.findByCortarNo(cortarNo)
            .orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다: " + cortarNo));
        crawlAndSaveArticlesForRegion(region);
    }

    /**
     * 특정 지역의 매물 정보를 수집하고 저장합니다.
     * 
     * @param region 지역 정보
     */
    public void crawlAndSaveArticlesForRegion(Region region) {
        String regionName = region.getCortarName();
        log.info("[Thread-{}] \n[{}] 매물 정보 수집 시작", Thread.currentThread().getId(), regionName);
        
        // 크롤링 상태 초기화
        CrawlingStatusDTO status = CrawlingStatusDTO.initialize(regionName);
        regionStatuses.put(regionName, status);
        
        region.setNaverStatus(Region.CrawlStatus.PROCESSING);
        region.setNaverLastCrawledAt(LocalDateTime.now());
        regionRepository.save(region);
        
        try {
            int currentPage = 1;
            boolean isRegionCompleted = false;
            AtomicInteger totalArticles = new AtomicInteger(0);
            
            // 첫 페이지 처리
            PageResultDTO firstPage = crawlPage(region.getCortarNo(), 1);
            if (!firstPage.isSuccess()) {
                throw new RuntimeException("첫 페이지 수집 실패: " + firstPage.getError());
            }
            
            totalArticles.addAndGet(saveArticles(firstPage.getArticles(), region));
            processPage(region, 1, firstPage.isHasMore());
            
            // 첫 페이지에 더 이상 데이터가 없으면 바로 종료
            if (!firstPage.isHasMore()) {
                isRegionCompleted = true;
                log.info("[Thread-{}] [{}] 첫 페이지에 더 이상 데이터가 없어 수집 완료", Thread.currentThread().getId(), regionName);
            }
            
            // 페이지 순차 처리
            while (!isRegionCompleted) {
                currentPage++;
                PageResultDTO result = crawlPage(region.getCortarNo(), currentPage);
                
                if (result.isSuccess()) {
                    totalArticles.addAndGet(saveArticles(result.getArticles(), region));
                    log.info("[Thread-{}] [{}] 페이지 {} 처리 완료 (현재 {}개 매물) {}", 
                        Thread.currentThread().getId(), regionName, currentPage, totalArticles.get(),
                        result.isHasMore() ? "▶" : "■");
                    
                    processPage(region, currentPage, result.isHasMore());
                    
                    if (!result.isHasMore()) {
                        isRegionCompleted = true;
                        log.info("[Thread-{}] [{}] 페이지 {}에서 더 이상 데이터가 없어 수집 완료", 
                            Thread.currentThread().getId(), regionName, currentPage);
                    }
                } else {
                    if (result.isProxyError()) {
                        log.warn("[Thread-{}] [{}] 페이지 {} 프록시 오류: {}", 
                            Thread.currentThread().getId(), regionName, currentPage, result.getError());
                        // 프록시 오류 시 짧은 대기 후 재시도
                        RandomSleepUtil.sleepShort();
                    } else {
                        log.warn("[Thread-{}] [{}] 페이지 {} 처리 실패 (스킵): {}", 
                            Thread.currentThread().getId(), regionName, currentPage, result.getError());
                        isRegionCompleted = true;
                    }
                }
            }
            
            // 최종 상태 업데이트
            region.setNaverStatus(Region.CrawlStatus.COMPLETED);
            log.info("[Thread-{}] [{}] 매물 정보 수집 완료 - 총 {}개 매물", 
                Thread.currentThread().getId(), regionName, totalArticles.get());
            
        } catch (Exception e) {
            log.error("[Thread-{}] [{}] 매물 정보 수집 중 오류 발생: {}", 
                Thread.currentThread().getId(), regionName, e.getMessage());
            region.setNaverStatus(Region.CrawlStatus.FAILED);
        } finally {
            region.setNaverLastCrawledAt(LocalDateTime.now());
            regionRepository.save(region);
        }
    }
    
    private PageResultDTO crawlPage(Long cortarNo, int page) {
        try {
            String apiUrl = buildApiUrl(cortarNo, page);
            String response = getArticlesWithProxy(apiUrl);
            
            if (response != null && !response.isEmpty()) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode articlesNode = root.path("body");
                boolean hasMore = root.path("more").asBoolean();
                
                List<JsonNode> articles = articlesNode.isArray() ? 
                    StreamSupport.stream(articlesNode.spliterator(), false)
                        .collect(Collectors.toList()) : 
                    Collections.emptyList();
                
                return PageResultDTO.success(page, articles, hasMore);
            }
            
            return PageResultDTO.failure(page, "빈 응답", true);
                
        } catch (Exception e) {
            boolean isProxyError = e instanceof IOException || 
                                 e.getMessage().contains("timeout") ||
                                 e.getMessage().contains("connection");
            
            return PageResultDTO.failure(page, e.getMessage(), isProxyError);
        }
    }
    
    @Transactional
    private int saveArticles(List<JsonNode> articles, Region region) {
        int count = 0;
        for (JsonNode article : articles) {
            try {
                saveArticle(article, region);
                count++;
            } catch (Exception e) {
                log.error("[Thread-{}] [{}] 매물 저장 중 오류 발생: {}", Thread.currentThread().getId(), region.getCortarName(), e.getMessage());
            }
        }
        return count;
    }

    private void handleRetry(Long cortarNo, int failedPage) {
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("재시도 대기 중 인터럽트 발생", e);
        }
    }

    /**
     * API URL을 생성하고 검증합니다.
     * 
     * @param cortarNo 지역 코드
     * @param page 페이지 번호
     * @return 검증된 API URL
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
        
        log.info("[Thread-{}] [{}] URL 생성 - 중심좌표: ({}, {}), 범위: top={}, right={}, bottom={}, left={}",
            Thread.currentThread().getId(), region.getCortarName(),
            region.getCenterLat(),
            region.getCenterLon(),
            top, right, bottom, left
        );
        
        String url = String.format("%s?itemId=&mapKey=&lgeo=&showR0=" +
            "&rletTpCd=APT:OPST:VL:YR:DSD:ABYG:OBYG:JGC:JWJT:DDDGG:SGJT:HOJT:JGB:OR:GSW:SG:SMS:GJCG:GM:TJ:APTHGJ" +
            "&tradTpCd=A1:B1:B2:B3" +
            "&z=%d&lat=%.6f&lon=%.6f&btm=%.6f&lft=%.6f&top=%.6f&rgt=%.6f" +
            "&cortarNo=%d&sort=rank&page=%d",
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
        
        // URL 유효성 검증
        try {
            new java.net.URL(url);
        } catch (java.net.MalformedURLException e) {
            log.error("[Thread-{}] [{}] 잘못된 URL 형식: {}", Thread.currentThread().getId(), region.getCortarName(), url);
            throw new RuntimeException("잘못된 URL 형식", e);
        }
        
        // URL 길이 검증
        if (url.length() > 2048) {
            log.warn("[Thread-{}] [{}] URL 길이가 너무 깁니다: {} 문자", Thread.currentThread().getId(), region.getCortarName(), url.length());
        }
        
        // 필수 파라미터 검증
        if (!url.contains("cortarNo=") || !url.contains("page=") || 
            !url.contains("lat=") || !url.contains("lon=")) {
            log.error("[Thread-{}] [{}] 필수 파라미터 누락: {}", Thread.currentThread().getId(), region.getCortarName(), url);
            throw new RuntimeException("필수 파라미터가 누락되었습니다");
        }
        
        return url;
    }
    
    /**
     * 프록시를 통해 네이버 부동산 API를 호출합니다.
     */
    private String getArticlesWithProxy(String apiUrl) {
        ProxyInfoDTO proxy = null;
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount < maxRetryCount) {
            try {
                // 프록시 풀에서 다음 프록시 가져오기 (로테이션)
                proxy = proxyPool.getNextAvailableProxy();
                if (proxy == null) {
                    log.warn("사용 가능한 프록시가 없습니다. 프록시 풀을 새로고침합니다.");
                    proxyPool.refreshProxyPool();
                    proxy = proxyPool.getNextAvailableProxy();
                }
                
                if (proxy == null) {
                    throw new RuntimeException("프록시를 사용할 수 없습니다.");
                }
                
                // API 요청 시작 시간 기록
                long startTime = System.currentTimeMillis();
                
                // API 요청
                java.net.URL url = new java.net.URL(apiUrl);
                java.net.Proxy proxyObj = new java.net.Proxy(
                    java.net.Proxy.Type.HTTP,
                    new java.net.InetSocketAddress(proxy.getHost(), proxy.getPort())
                );
                
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection(proxyObj);
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
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                
                int responseCode = conn.getResponseCode();
                log.info("[Thread-{}] [프록시: {}] 응답 코드: {}", Thread.currentThread().getId(), proxy.getKey(), responseCode);
                
                if (responseCode == 200) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        
                        // 성공한 프록시의 응답 시간 업데이트
                        long responseTime = System.currentTimeMillis() - startTime;
                        proxy = proxy.withUpdatedResponseTime(responseTime);
                        proxyPool.releaseProxy(proxy);
                        
                        return response.toString();
                    }
                } else {
                    log.warn("[Thread-{}] [프록시: {}] 응답 실패. 응답 코드: {}", 
                        Thread.currentThread().getId(), proxy.getKey(), responseCode);
                    proxyPool.markProxyAsFailed(proxy);
                    retryCount++;
                }
            } catch (Exception e) {
                lastException = e;
                log.error("[Thread-{}] [프록시: {}] 요청 중 오류 발생: {}", 
                    Thread.currentThread().getId(), 
                    proxy != null ? proxy.getKey() : "unknown", e.getMessage());
                if (proxy != null) {
                    proxyPool.markProxyAsFailed(proxy);
                }
                retryCount++;
            }
            
            if (retryCount < maxRetryCount) {
                try {
                    // 재시도 횟수에 따라 대기 시간을 점진적으로 증가
                    long delay = retryDelayMs * (1 + retryCount);
                    log.info("[Thread-{}] {}초 후 재시도... (시도 {}/{})", 
                        Thread.currentThread().getId(), delay/1000, retryCount, maxRetryCount);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("재시도 대기 중 인터럽트 발생", e);
                }
            }
        }
        
        throw new RuntimeException("프록시를 통한 요청 실패. 마지막 오류: " + 
            (lastException != null ? lastException.getMessage() : "알 수 없음"));
    }

    @Transactional
    private void saveArticle(JsonNode articleNode, Region region) {
        try {
            String articleId = articleNode.path("atclNo").asText();
            Optional<PropertyArticle> existingArticle = propertyArticleRepository.findByArticleId(articleId);
            
            PropertyArticle article = existingArticle.orElseGet(() -> {
                PropertyArticle newArticle = new PropertyArticle();
                newArticle.setArticleId(articleId);
                newArticle.setRegionCode(String.valueOf(region.getCortarNo()));
                newArticle.setPlatform(PropertyArticle.Platform.NAVER);
                newArticle.setPlatformUrl("https://new.land.naver.com/articles/" + articleId);
                newArticle.setCreatedAt(LocalDateTime.now());
                return newArticle;
            });
            
            // 기본 정보 업데이트
            article.setBuildingName(articleNode.path("atclNm").asText());
            article.setDescription(articleNode.path("atclFetrDesc").asText());
            article.setBuildingType(articleNode.path("rletTpNm").asText());
            
            // 거래 유형 및 가격 정보 설정
            String tradTpNm = articleNode.path("tradTpNm").asText();
            switch (tradTpNm) {
                case "매매":
                    article.setCategory(PropertyArticle.Category.SALE);
                    article.setPrice(articleNode.path("prc").asLong());
                    break;
                case "전세":
                    article.setCategory(PropertyArticle.Category.DEPOSIT);
                    article.setDeposit(articleNode.path("prc").asLong());
                    break;
                case "월세":
                    article.setCategory(PropertyArticle.Category.MONTHLY);
                    article.setDeposit(articleNode.path("prc").asLong());
                    article.setMonthlyRent(articleNode.path("rentPrc").asLong());
                    break;
            }
            
            // 위치 및 면적 정보 업데이트
            article.setLongitude(articleNode.path("lon").asDouble());
            article.setLatitude(articleNode.path("lat").asDouble());
            article.setSupplyArea(articleNode.path("spc1").asDouble());
            article.setExclusiveArea(articleNode.path("spc2").asDouble());
            article.setUpdatedAt(LocalDateTime.now());
            
            propertyArticleRepository.save(article);
            
        } catch (Exception e) {
            log.error("[Thread-{}] [{}] 매물 정보 저장 중 오류 발생: {}", Thread.currentThread().getId(), region.getCortarName(), e.getMessage(), e);
            throw new RuntimeException("매물 정보 저장 실패", e);
        }
    }

    private void processPage(Region region, int page, boolean hasMore) {
        // 대기 시간을 페이지 처리 결과에 따라 동적으로 조정
        if (!hasMore) {
            RandomSleepUtil.sleepShort();
        } else {
            // 연속된 페이지 처리 시 짧은 대기
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
} 
