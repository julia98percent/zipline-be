package com.zipline.service.naver.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.naver.NaverRawArticle;
import com.zipline.domain.entity.region.Region;
import com.zipline.global.util.CoordinateUtil;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.infrastructure.crawl.CrawlRepository;
import com.zipline.infrastructure.naver.NaverRawArticleRepository;
import com.zipline.infrastructure.region.RegionRepository;
import com.zipline.service.naver.client.ProxyNaverApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyNaverRegionCrawler {

    private final NaverRawArticleRepository articleRepo;
    private final RegionRepository regionRepo;
    private final CrawlRepository crawlRepo;
    private final ProxyNaverApiClient proxyNaverApiClient;
    private static final int RECENT_DAYS = 14;
    private static final int PAGE_SIZE = 100;

    /**
     * 전체 지역에 대한 크롤링 실행 (병렬 처리 가능)
     */
    public void executeCrawl(Object ignored) {
        log.info("=== 네이버 프록시를 통한 원본 매물 정보 수집 시작 ===");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RECENT_DAYS);

        int pageNumber = 0;
        boolean hasMore = true;

        while (hasMore) {
            Page<Long> regions = crawlRepo.findRegionsNeedingCrawlingUpdateForNaverWithPage(cutoffDate, PageRequest.of(pageNumber, PAGE_SIZE));
            if (regions.isEmpty()) break;

            regions.getContent().forEach(region -> {
                executeCrawlForRegion(region);
                crawlRepo.updateNaverLastCrawledAt(region, LocalDateTime.now());
            });

            pageNumber++;
            hasMore = pageNumber < regions.getTotalPages();
        }

        log.info("=== 네이버 프록시를 통한 원본 매물 정보 수집 완료 ===");
    }

    /**
     * 특정 지역에 대한 크롤링 실행
     */
    public void executeCrawlForRegion(Long cortarNo) {
        log.info("[{}] 네이버 프록시 기반 매물 수집 시작", cortarNo);
        crawlRepo.updateNaverCrawlStatus(cortarNo, CrawlStatus.PROCESSING);
        articleRepo.resetMigrationStatusForRegion(cortarNo, MigrationStatus.PENDING);

        ObjectMapper objectMapper = new ObjectMapper();
        int page = 1;
        boolean hasMore = true;
        int total = 0;

        try {
            Region region = regionRepo.findByCortarNo(cortarNo)
                    .orElseThrow(() -> new RuntimeException("지역 없음: " + cortarNo));

            while (hasMore) {
                String apiUrl = buildApiUrl(region, page++);
                String response = proxyNaverApiClient.fetchArticleList(apiUrl);

                if (response != null && !response.isEmpty()) {
                    JsonNode result = objectMapper.readTree(response);
                    JsonNode articles = result.path("body");

                    if (articles.isArray()) {
                        for (JsonNode node : articles) {
                            saveRawArticle(node, cortarNo);
                            total++;
                        }
                    }

                    hasMore = result.path("more").asBoolean();
                    RandomSleepUtil.sleepShort(); // Add delay between requests
                } else {
                    log.warn("[{}] 페이지 {}에서 빈 응답 수신", cortarNo, page - 1);
                    hasMore = false; // Stop further processing
                }
            }

            crawlRepo.updateNaverCrawlStatus(cortarNo, CrawlStatus.COMPLETED);
            log.info("[{}] 완료 - 총 매물 수: {}", cortarNo, total);

        } catch (Exception e) {
            log.error("[{}] 처리 실패", cortarNo, e);
            crawlRepo.updateNaverCrawlStatus(cortarNo, CrawlStatus.FAILED);
        }
    }

    private String buildApiUrl(Region region, int page) {
        double[] bounds = CoordinateUtil.calculateBounds(
                region.getCenterLat(),
                region.getCenterLon(),
                12
        );

        return String.format(
                "https://m.land.naver.com/cluster/ajax/articleList?" +
                        "rletTpCd=APT:OPST:VL:YR:DSD:ABYG:OBYG:JGC:JWJT:DDDGG:SGJT:HOJT:JGB:OR:GSW:SG:SMS:GJCG:GM:TJ:APTHGJ&" +
                        "tradTpCd=A1:B1:B2:B3&z=12&lat=%.6f&lon=%.6f&btm=%.6f&lft=%.6f&top=%.6f&rgt=%.6f&cortarNo=%d&sort=rank&page=%d",
                region.getCenterLat(),
                region.getCenterLon(),
                bounds[2],
                bounds[3],
                bounds[0],
                bounds[1],
                region.getCortarNo(),
                page
        );
    }

    private void saveRawArticle(JsonNode node, Long cortarNo) {
        String articleId = node.path("atclNo").asText();
        Optional<NaverRawArticle> existing = articleRepo.findByArticleId(articleId);

        NaverRawArticle raw = existing.map(e -> NaverRawArticle.builder()
                        .id(e.getId())
                        .articleId(articleId)
                        .cortarNo(cortarNo)
                        .rawData(node.toString())
                        .migrationStatus(MigrationStatus.PENDING)
                        .createdAt(e.getCreatedAt())
                        .build())
                .orElseGet(() -> NaverRawArticle.builder()
                        .articleId(articleId)
                        .cortarNo(cortarNo)
                        .rawData(node.toString())
                        .migrationStatus(MigrationStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build());

        articleRepo.save(raw);
    }
}
