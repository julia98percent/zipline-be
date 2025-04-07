package com.zipline.controller.publicItem;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.entity.publicItem.Region;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.global.util.ProxyPool;
import com.zipline.repository.publicItem.RegionRepository;
import com.zipline.service.publicItem.ProxyNaverArticleService;
import com.zipline.dto.publicItem.ProxyStatusDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/crawl/naver_p")
@RequiredArgsConstructor
public class NaverProxyCrawlController {

    private final ProxyNaverArticleService proxyNaverArticleService;
    private final CrawlingStatusManager crawlingStatusManager;
    private final ProxyPool proxyPool;
    private final RegionRepository regionRepository;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Void>> crawlAllArticleFromNaverWithProxy() {
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                proxyNaverArticleService.crawlAndSaveArticlesByLevel(3);
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok("프록시를 통한 레벨 3 매물 정보 수집이 시작되었습니다."));
    }

    @GetMapping("/{cortarNo}")
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

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<ProxyStatusDTO>> getProxyStatus() {
        ProxyStatusDTO status = ProxyStatusDTO.of(
            proxyPool.getAvailableProxyCount(),
            proxyPool.getInUseProxyCount(),
            proxyPool.getActiveProxies()
        );
        return ResponseEntity.ok(ApiResponse.ok("프록시 상태 조회", status));
    }
}
