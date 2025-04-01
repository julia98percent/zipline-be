package com.zipline.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.service.NaverArticleService;
import com.zipline.service.RegionCodeService;
import com.zipline.util.CrawlingStatusManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/crawl")
@RequiredArgsConstructor
public class CrawlerController {

    private final RegionCodeService regionCodeService;
    private final NaverArticleService naverArticleService;
    private final CrawlingStatusManager crawlingStatusManager;

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
} 