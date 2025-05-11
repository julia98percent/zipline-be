package com.zipline.service.naver.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.crawl.Crawl;
import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.enums.Platform;
import com.zipline.domain.entity.naver.NaverRawArticle;
import com.zipline.domain.entity.region.Region;
import com.zipline.global.util.CoordinateUtil;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.infrastructure.crawl.CrawlRepository;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import com.zipline.infrastructure.naver.NaverRawArticleRepository;
import com.zipline.infrastructure.region.RegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service("naverArticleCrawler")
public class NaverArticleCrawler {

    private final ObjectMapper objectMapper;
    private final RegionRepository regionRepo;
    private final NaverRawArticleRepository articleRepo;
    private final CrawlRepository crawlRepo;

    @Value("${crawler.recent-days:14}")
    private int recentDays;

    @Value("${crawler.page-size:100}")
    private int pageSize;

    @Value("${crawler.max-retry-count:10}")
    private int maxRetryCount;

    public NaverArticleCrawler(
            ObjectMapper objectMapper,
            CrawlRepository crawlRepo,
            RegionRepository regionRepo,
            NaverRawArticleRepository articleRepo
    ) {
        this.objectMapper = objectMapper;
        this.crawlRepo = crawlRepo;
        this.regionRepo = regionRepo;
        this.articleRepo = articleRepo;
    }

    public void executeCrawl(Fetcher fetcher) {
        log.info("=== 네이버 원본 매물 정보 수집 시작 ===");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(recentDays);

        int pageNumber = 0;
        boolean hasMore = true;

        while (hasMore) {
            Page<Long> regions = crawlRepo.findRegionsNeedingCrawlingUpdateForNaverWithPage(cutoffDate,
                    PageRequest.of(pageNumber, pageSize));
            if (regions.isEmpty()) break;

            regions.getContent().forEach(region -> {
                executeCrawlForRegion(fetcher,region);
            });

            pageNumber++;
            hasMore = pageNumber < regions.getTotalPages();
        }

        log.info("=== 네이버 원본 매물 정보 수집 완료 ===");
    }

    @Transactional
    public void executeCrawlForRegion(Fetcher fetcher, Long cortarNo) {
        log.info("네이버 원본 매물 수집 시작 - 지역 코드: {}", cortarNo);
        crawlRepo.updateNaverCrawlStatus(cortarNo, CrawlStatus.PROCESSING);

        ObjectMapper objectMapper = new ObjectMapper();
        int page = 1;
        boolean hasMore = true;
        boolean is307 = false;
        int total = 0;

        FetchConfigDTO fetchConfig = FetchConfigDTO.builder()
                .accept("application/json")
                .host("m.land.naver.com")
                .referer("https://m.land.naver.com/")
                .secChUa("\"Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122\"")
                .secChUaMobile("?1")
                .secChUaPlatform("\"Android\"")
                .secFetchDest("empty")
                .secFetchMode("cors")
                .secFetchSite("same-origin")
                .userAgent("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
                .acceptLanguage("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .connectTimeout(5000)
                .readTimeout(10000)
                .build();

        try {
            Crawl crawlRegion = crawlRepo.findByCortarNo(cortarNo);

            while (hasMore) {
                String apiUrl = buildApiUrl(crawlRegion, page++);
                String response = fetcher.fetch(apiUrl, fetchConfig);

                if (response == "307") {
                    String errorMessage = String.format("[ERROR307]"+"네이버 원본 데이터 조회 실패 307: API URL: %s", apiUrl);
                    log.warn(errorMessage);
                    crawlRegion.errorWithLog(Platform.NAVER, errorMessage, 1000, CrawlStatus.ERROR307);
                    crawlRepo.save(crawlRegion);
                    is307 = true;
                    break;
                }

                if (!response.isEmpty()) {
                    JsonNode result = objectMapper.readTree(response);
                    JsonNode articles = result.path("body");

                    if (articles.isArray()) {
                        for (JsonNode node : articles) {
                            saveRawArticle(node, cortarNo);
                            total++;
                        }
                    }
                    hasMore = result.path("more").asBoolean();
                    RandomSleepUtil.sleep();
                }
            }
            LocalDateTime now = LocalDateTime.now();
            if (!is307) crawlRepo.updateNaverCrawlStatusAndLastCrawledAt(cortarNo, CrawlStatus.COMPLETED, now);
            log.info("완료 - 지역: {}, 총 매물 수: {}", crawlRegion.getCortarNo(), total);
        } catch (Exception e) {
            String errorMessage = String.format("[FAILED]"+"지역 %s 처리 실패: %s", cortarNo, e.getMessage());
            log.error(errorMessage, e);
            Crawl crawl = crawlRepo.findByCortarNo(cortarNo);
            crawl.errorWithLog(Platform.NAVER, errorMessage, 1000, CrawlStatus.FAILED);
            crawlRepo.save(crawl);
        }
    }

    private String buildApiUrl(Crawl crawlRegion, int page) {
        Optional<Region> region = regionRepo.findByCortarNo(crawlRegion.getCortarNo());
        Double lat = region.map(Region::getCenterLat).orElse(null);
        Double lon = region.map(Region::getCenterLon).orElse(null);

        if (lat == null || lon == null) {
            log.error("지역 좌표 정보 없음: {}", crawlRegion.getCortarNo());
            throw new RuntimeException("지역 좌표 정보 없음: " + crawlRegion.getCortarNo());
        }

        double[] bounds = CoordinateUtil.calculateBounds(
                lat,
                lon,
                12
        );
        return String.format(
                "https://m.land.naver.com/cluster/ajax/articleList?" +
                        "rletTpCd=APT:OPST:VL:YR:DSD:ABYG:OBYG:JGC:JWJT:DDDGG:SGJT:HOJT:JGB:OR:GSW:SG:SMS:GJCG:GM:TJ:APTHGJ&" +
                        "tradTpCd=A1:B1:B2:B3&z=12&lat=%.6f&lon=%.6f&btm=%.6f&lft=%.6f&top=%.6f&rgt=%.6f&cortarNo=%d&sort=rank&page=%d",
                lat,
                lon,
                bounds[2],
                bounds[3],
                bounds[0],
                bounds[1],
                region.get().getCortarNo(),
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