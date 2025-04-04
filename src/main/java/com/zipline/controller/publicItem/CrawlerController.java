package com.zipline.controller.publicItem;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.publicItem.ProxyStatusDTO;
import com.zipline.entity.publicItem.Region;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.util.ProxyPool;
import com.zipline.repository.publicItem.RegionRepository;
import com.zipline.service.publicItem.NaverArticleService;
import com.zipline.service.publicItem.ProxyNaverArticleService;
import com.zipline.service.publicItem.RegionCodeService;
import com.zipline.global.util.CrawlingStatusManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/crawl")
@RequiredArgsConstructor
public class CrawlerController {

    private final RegionCodeService regionCodeService;
    private final NaverArticleService naverArticleService;
    private final ProxyNaverArticleService proxyNaverArticleService;
    private final ProxyPool proxyPool;
    private final CrawlingStatusManager crawlingStatusManager;
    private final RegionRepository regionRepository;

    @GetMapping("/regions")
    public ResponseEntity<ApiResponse<Void>> crawlRegions() {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                regionCodeService.crawlAndSaveRegions();
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("지역 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/articles/level/{level}")
    public ResponseEntity<ApiResponse<Void>> crawlArticlesByLevel(@PathVariable int level) {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                naverArticleService.crawlAndSaveArticlesByLevel(level);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("레벨 " + level + " 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/articles/naver/all")
    public ResponseEntity<ApiResponse<Void>> crawlAllArticleFromNaver() {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                naverArticleService.crawlAndSaveArticlesByLevel(3);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("레벨 " + 3 + " 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/articles/region/{cortarNo}")
    public ResponseEntity<ApiResponse<Void>> crawlArticlesByRegion(@PathVariable Long cortarNo) {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                naverArticleService.crawlAndSaveArticlesForRegion(cortarNo);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("지역 " + cortarNo + " 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> getCrawlingStatus() {
        return ResponseEntity.ok(ApiResponse.ok("크롤링 상태 조회", crawlingStatusManager.isCrawling()));
    }

    @GetMapping("/proxy/articles/naver/all")
    public ResponseEntity<ApiResponse<Void>> crawlAllArticleFromNaverWithProxy() {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                proxyNaverArticleService.crawlAndSaveArticlesByLevel(3);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("프록시를 통한 레벨 3 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/proxy/articles/region/{cortarNo}")
    public ResponseEntity<ApiResponse<Void>> crawlArticlesWithProxyByRegion(@PathVariable Long cortarNo) {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                Optional<Region> region = regionRepository.findByCortarNo(cortarNo);
                if (region.isPresent()) {
                    proxyNaverArticleService.crawlAndSaveArticlesForRegion(region.get());
                } else {
                    log.error("지역을 찾을 수 없습니다: {}", cortarNo);
                }
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("프록시를 통한 지역 " + cortarNo + " 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/proxy/status")
    public ResponseEntity<ApiResponse<ProxyStatusDTO>> getProxyStatus() {
        ProxyStatusDTO status = ProxyStatusDTO.of(
            proxyPool.getAvailableProxyCount(),
            proxyPool.getInUseProxyCount(),
            proxyPool.getActiveProxies()
        );
        return ResponseEntity.ok(ApiResponse.ok("프록시 상태 조회", status));
    }
} 
