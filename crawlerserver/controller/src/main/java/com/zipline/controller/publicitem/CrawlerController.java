package com.zipline.controller.publicitem;

import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/crawl")
@RequiredArgsConstructor
public class CrawlerController {
    private final CrawlingStatusManager crawlingStatusManager;
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> getCrawlingStatus() {
        return ResponseEntity.ok(ApiResponse.ok("크롤링 상태 조회", crawlingStatusManager.isCrawling()));
    }
   } 
