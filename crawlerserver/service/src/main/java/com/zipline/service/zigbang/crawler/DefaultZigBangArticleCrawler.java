package com.zipline.service.zigbang.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.enums.PropertyCategory;
import com.zipline.domain.entity.zigbang.ZigBangArticle;
import com.zipline.domain.entity.zigbang.ZigBangCrawl;
import com.zipline.global.util.GeoHashGenerator;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.global.util.UrlEncodingUtil;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;
import com.zipline.infrastructure.zigbang.ZigBangArticleRepository;
import com.zipline.infrastructure.zigbang.ZigBangCrawlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("defaultZigBangArticleCrawler")
@RequiredArgsConstructor
public class DefaultZigBangArticleCrawler implements ZigBangArticleCrawler {

    private final SharedZigbangCrawler shared = new SharedZigbangCrawler();

    private static final int LIMIT = 20;
    private static final int GEOHASH_PRECISION = 5;

    // 대한민국 위도/경도 범위
    private static final double MIN_LAT = 33.0;
    private static final double MAX_LAT = 38.6;
    private static final double MIN_LON = 124.5;
    private static final double MAX_LON = 132.0;

    // 격자 간격
    private static final double LAT_STEP = 0.05;
    private static final double LON_STEP = 0.05;

    private final ObjectMapper objectMapper;
    private final ZigBangCrawlRepository crawlRepo;
    private final ZigBangArticleRepository articleRepo;

    @Override
    public void executeCrawl(Fetcher fetcher) {
        log.info("=== 직방 전체 매물 정보 수집 시작 ===");

        Set<String> koreaGeohashes = GeoHashGenerator.generateGeoHashs(
                GEOHASH_PRECISION, MIN_LAT, MAX_LAT, MIN_LON, MAX_LON, LAT_STEP, LON_STEP);

        for (PropertyCategory category : PropertyCategory.values()) {
            for (String geohash : koreaGeohashes) {
                try {
                    if (isAlreadyCompleted(geohash, category)) {
                        log.info("이미 완료된 geohash: {}, category: {}", geohash, category);
                        continue;
                    }

                    List<Long> itemIds = fetchItemIds(fetcher, geohash, category);

                    if (!itemIds.isEmpty()) {
                        fetchAndSaveItemDetails(fetcher, geohash, category, itemIds);
                        updateCrawlComplete(geohash, category);
                    }

                } catch (Exception e) {
                    String errorMsg = String.format("지오해시 %s, 카테고리 %s 처리 실패: %s", geohash, category, e.getMessage());
                    log.error(errorMsg, e);
                    updateCrawlError(geohash, category, errorMsg);
                }
            }
        }

        log.info("=== 직방 전체 매물 정보 수집 완료 ===");
    }

    private List<Long> fetchItemIds(Fetcher fetcher, String geohash, PropertyCategory category) throws Exception {
        FetchConfigDTO config = FetchConfigDTO.zigbangDefaultConfig();
        int offset = 0;
        boolean hasMore = true;
        List<Long> itemIds = new ArrayList<>();

        log.info("직방 매물 ID 수집 시작 - geohash: {}, category: {}", geohash, category);
        ZigBangCrawl crawl = ZigBangCrawl.create(geohash, category).updateStatus(CrawlStatus.PROCESSING);
        crawlRepo.save(crawl);

        while (hasMore) {
            String listUrl = buildListUrl(category, geohash) + "&offset=" + offset + "&limit=" + LIMIT;
            String response = fetcher.fetch(listUrl, config);

            if (response != null && !response.isEmpty()) {
                List<Long> ids = parseItemIdsFromResponse(response);
                itemIds.addAll(ids);
                hasMore = checkHasMore(response);
                offset += LIMIT;
            } else {
                log.warn("지오해시 {} / 카테고리 {} 요청 실패 → 빈 리스트로 대체", geohash, category);
                return List.of(); // 🚫 400 에러 시 재시도 X, 빈 리스트 반환
            }

            RandomSleepUtil.sleep();
        }

        log.info("직방 매물 ID 수집 완료 - geohash: {}, category: {}, 총 건수: {}", geohash, category, itemIds.size());
        return itemIds;
    }

    private void fetchAndSaveItemDetails(Fetcher fetcher, String geohash, PropertyCategory category, List<Long> itemIds) throws Exception {
        log.info("직방 매물 상세 정보 수집 시작 - geohash: {}, category: {}, 총 건수: {}", geohash, category, itemIds.size());

        FetchConfigDTO config = FetchConfigDTO.zigbangDefaultConfig();

        for (Long itemId : itemIds) {
            try {
                String detailUrl = buildDetailUrl(itemId); // https://api.zigbang.com/v3/items/123456
                String response = fetcher.fetch(detailUrl, config);

                if (response != null && !response.isEmpty()) {
                    saveRawArticle(geohash, category, itemId, response);
                }

                RandomSleepUtil.sleep();

            } catch (Exception e) {
                log.error("상세 정보 수집 실패 - item_id: {}, 오류: {}", itemId, e.getMessage());
            }
        }

        log.info("직방 매물 상세 정보 수집 완료 - geohash: {}, category: {}", geohash, category);
    }

    private List<Long> parseItemIdsFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemsNode = rootNode.get("items");

            List<Long> ids = new ArrayList<>();
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode node : itemsNode) {
                    JsonNode idNode = node.get("item_id");
                    if (idNode != null && idNode.isNumber()) {
                        ids.add(idNode.asLong());
                    }
                }
            }

            return ids;
        } catch (Exception e) {
            log.warn("item_id 파싱 실패: {}", e.getMessage());
            return List.of();
        }
    }

    private void saveRawArticle(String geohash, PropertyCategory category, Long itemId, String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemNode = rootNode.get("item");

            ZigBangArticle article = new ZigBangArticle();
            article.create(itemId.toString(), geohash, category, itemNode.toString());
            articleRepo.save(article);

            log.info("직방 매물 상세데이터 저장 완료 - geohash: {}, item_id: {}, category: {}", geohash, itemId, category);

        } catch (Exception e) {
            log.error("직방 매물 저장 실패 - geohash: {}, item_id: {}, 오류: {}", geohash, itemId, e.getMessage());
        }
    }

    private boolean checkHasMore(String response) {
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(response, Map.class);
            return Boolean.TRUE.equals(jsonMap.get("hasNext"));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAlreadyCompleted(String geohash, PropertyCategory category) {
        return crawlRepo.findById(ZigBangCrawl.buildId(geohash, category))
                .map(c -> c.getStatus() == CrawlStatus.COMPLETED)
                .orElse(false);
    }

    private void updateCrawlComplete(String geohash, PropertyCategory category) {
        crawlRepo.findById(ZigBangCrawl.buildId(geohash, category))
                .map(c -> c.updateStatus(CrawlStatus.COMPLETED))
                .ifPresent(c -> crawlRepo.save(c));
    }

    private void updateCrawlError(String geohash, PropertyCategory category, String message) {
        ZigBangCrawl crawl = crawlRepo.findById(ZigBangCrawl.buildId(geohash, category))
                .orElse(ZigBangCrawl.create(geohash, category));

        crawlRepo.save(crawl.errorWithLog(message, 1000, CrawlStatus.FAILED));
    }

    public String buildListUrl(PropertyCategory category, String geohash) {
        return shared.buildListUrl(category, geohash);
    }

    public String buildDetailUrl(Long itemId) {
        return shared.buildDetailUrl(itemId);
    }

}