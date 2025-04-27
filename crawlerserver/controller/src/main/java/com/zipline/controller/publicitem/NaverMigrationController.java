package com.zipline.controller.publicitem;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.publicItem.NaverRawArticleMigrationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crawl/naver-migration")
@RequiredArgsConstructor
public class NaverMigrationController {

	private final NaverRawArticleMigrationService migrationService;
	private final CrawlingStatusManager crawlingStatusManager;

	@GetMapping
	public ResponseEntity<ApiResponse<Void>> startMigration() {
		CompletableFuture.runAsync(() -> {
			crawlingStatusManager.executeWithLock(() -> {
				migrationService.NaverMigration();
				return null;
			});
		});
		return ResponseEntity.ok(ApiResponse.ok("네이버 원본 매물 데이터 마이그레이션이 시작되었습니다."));
	}

	@GetMapping("/region/{cortarNo}")
	public ResponseEntity<ApiResponse<Void>> migrateRegion(@PathVariable Long cortarNo) {
		CompletableFuture.runAsync(() -> {
			crawlingStatusManager.executeWithLock(() -> {
				migrationService.migrateRawArticlesForRegion(cortarNo);
				return null;
			});
		});
		return ResponseEntity.ok(ApiResponse.ok("지역 " + cortarNo + " 네이버 원본 매물 데이터 마이그레이션이 시작되었습니다."));
	}

	@GetMapping("/retry")
	public ResponseEntity<ApiResponse<Void>> retryFailedMigrations() {
		CompletableFuture.runAsync(() -> {
			crawlingStatusManager.executeWithLock(() -> {
				migrationService.retryFailedMigrations();
				return null;
			});
		});
		return ResponseEntity.ok(ApiResponse.ok("실패한 마이그레이션 재시도가 시작되었습니다."));
	}
}
