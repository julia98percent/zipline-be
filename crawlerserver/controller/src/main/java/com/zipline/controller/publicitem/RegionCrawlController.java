package com.zipline.controller.publicitem;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.publicItem.RegionCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/crawl/region")
@RequiredArgsConstructor
public class RegionCrawlController {

	private final RegionCodeService regionCodeService;
	private final CrawlingStatusManager crawlingStatusManager;

	@GetMapping
	public ResponseEntity<ApiResponse<Void>> crawlRegions() {
		CompletableFuture.runAsync(() -> {
			crawlingStatusManager.executeWithLock(() -> {
				regionCodeService.crawlAndSaveRegions();
				return null;
			});
		});
		return ResponseEntity.ok(ApiResponse.ok("지역 정보 수집이 시작되었습니다."));
	}
}
