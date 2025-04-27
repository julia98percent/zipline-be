package com.zipline.global.util;

import com.zipline.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import java.util.concurrent.CompletableFuture;

public class CrawlingStatusUtil {
    public static ResponseEntity<ApiResponse<Void>> checkAndExecute(
            CrawlingStatusManager crawlingStatusManager, Runnable task, String successMessage) {
        if (crawlingStatusManager.isCrawling()) {
            return ResponseEntity.ok(ApiResponse.ok("현재 다른 크롤링 작업이 진행 중입니다."));
        }
        CompletableFuture.runAsync(() -> {
            crawlingStatusManager.executeWithLock(() -> {
                task.run();
                return null;
            });
        });
        return ResponseEntity.ok(ApiResponse.ok(successMessage));
    }
}
