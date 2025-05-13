package com.zipline.service.zigbang.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.enums.PropertyCategory;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.infrastructure.zigbang.ZigBangArticleRepository;
import com.zipline.infrastructure.zigbang.ZigBangCrawlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

@Slf4j
@Service("parallelZigBangArticleCrawler")
public class ParallelZigBangArticleCrawler extends SharedZigbangCrawler {

    @Value("${crawler.max-concurrent-geohashes:5}")
    private int maxConcurrentGeohashes;


    public ParallelZigBangArticleCrawler(
            ObjectMapper objectMapper,
            ZigBangCrawlRepository crawlRepo,
            ZigBangArticleRepository articleRepo) {
        super(objectMapper, crawlRepo, articleRepo);
    }

    @Override
    public void executeCrawl(Fetcher fetcher) {
        log.info("=== 직방 병렬 매물 정보 수집 시작 ===");
        ExecutorService executor = Executors.newFixedThreadPool(maxConcurrentGeohashes);

        Set<String> geohashes = getGeohashList();
        AtomicInteger completedCount = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (PropertyCategory category : PropertyCategory.values()) {
            for (String geohash : geohashes) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        processGeoHash(fetcher, geohash, category, completedCount);
                    } catch (Exception e) {
                        String errorMsg = String.format("직방 병렬 수집 실패 - geohash: %s, category: %s, 오류: %s", geohash, category, e.getMessage());
                        updateCrawlError(geohash, category, errorMsg);
                        log.error(errorMsg, e);
                    }
                }, executor);

                futures.add(future);

                // 동시성 제한 체크
                if (futures.size() >= maxConcurrentGeohashes) {
                    CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).join();
                    futures.removeIf(CompletableFuture::isDone);
                }
            }
        }

        // 모든 작업 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("=== 직방 병렬 매물 정보 수집 완료 ===");
    }

    private void processGeoHash(Fetcher fetcher, String geohash, PropertyCategory category, AtomicInteger completedCount) throws Exception {
        if (isAlreadyCompleted(geohash, category)) {
            log.info("이미 완료된 geohash: {}, category: {}", geohash, category);
            return;
        }

        List<Long> itemIds = fetchItemIds(fetcher, geohash, category);

        if (!itemIds.isEmpty()) {
            fetchAndSaveItemDetails(fetcher, geohash, category, itemIds);
            updateCrawlComplete(geohash, category);
        }

        log.info("병렬 처리 완료 - geohash: {}, category: {}", geohash, category);
        completedCount.incrementAndGet();
    }
}