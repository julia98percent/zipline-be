package com.zipline.controller.publicItem;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.publicItem.NaverRawArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/crawl/naver-raw")
@RequiredArgsConstructor
public class NaverRawArticleController {

    private final NaverRawArticleService naverRawArticleService;
    private final CrawlingStatusManager crawlingStatusManager;

    @GetMapping("/articles/all")
    public ResponseEntity<ApiResponse<Void>> crawlAllRawArticleFromNaver() {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                naverRawArticleService.crawlAndSaveRawArticlesByLevel(3);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("레벨 " + 3 + " 원본 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/articles/{cortarNo}")
    public ResponseEntity<ApiResponse<Void>> crawlRawArticlesByRegion(@PathVariable Long cortarNo) {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                naverRawArticleService.crawlAndSaveRawArticlesForRegion(cortarNo);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("지역 " + cortarNo + " 원본 매물 정보 수집이 시작되었습니다."));
    }
}
