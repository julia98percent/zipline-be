package com.zipline.service.naver.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.infrastructure.crawl.CrawlRepository;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.infrastructure.naver.NaverRawArticleRepository;
import com.zipline.infrastructure.region.RegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service("parallelNaverArticleCrawler")
public class ParallelNaverArticleCrawler extends NaverArticleCrawler {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Value("${crawler.max-concurrent-regions:5}")
    private int MAX_CONCURRENT_REGIONS;

    @Value("${crawler.retry-delay-ms:1000}")
    private long retryDelayMs;

    @Value("${crawler.max-concurrent-requests:10}")
    private int MAX_CONCURRENT_REQUESTS;

    private int localRecentDays;

    private int localPageSize;

    private int localMaxRetryCount;

    @Autowired
    private CrawlRepository crawlRepo;

    public ParallelNaverArticleCrawler(
            ObjectMapper objectMapper,
            RegionRepository regionRepo,
            NaverRawArticleRepository articleRepo,
            @Value("${crawler.recent-days:14}") int recentDays,
            @Value("${crawler.page-size:100}") int pageSize,
            @Value("${crawler.max-retry-count:10}") int maxRetryCount) {
        super(objectMapper, regionRepo, articleRepo, null, recentDays, pageSize, maxRetryCount);
        this.localRecentDays = recentDays;
        this.localPageSize = pageSize;
        this.localMaxRetryCount = maxRetryCount;
    }

    @Override
    public void executeCrawl(Fetcher fetcher) {

        log.info("=== 프록시를 통한 네이버 병렬 크롤링 시작 ===");

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(localRecentDays);
        int pageNumber = 0;
        boolean hasMore = true;

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while (hasMore) {
            Page<Long> regions = crawlRepo.findRegionsNeedingCrawlingUpdateForNaverWithPage(cutoffDate,
                    PageRequest.of(pageNumber++, localPageSize));
            if (regions.isEmpty()) break;

            for (Long region : regions.getContent()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        executeCrawlForRegion(fetcher, region);
                        crawlRepo.updateNaverLastCrawledAt(region, LocalDateTime.now());
                    } catch (Exception e) {
                        log.error("병렬 크롤링 실패 - 지역: {}", region, e);
                    }
                }, executor);

                futures.add(future);

                // 동시성 제한
                if (futures.size() >= MAX_CONCURRENT_REGIONS) {
                    CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).join();
                    futures.removeIf(CompletableFuture::isDone);
                }

                RandomSleepUtil.sleepShort();
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("=== 프록시를 통한 네이버 병렬 크롤링 완료 ===");
    }
}
