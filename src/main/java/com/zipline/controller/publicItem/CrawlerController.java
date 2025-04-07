package com.zipline.controller.publicItem;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/crawl")
@RequiredArgsConstructor
public class CrawlerController {
    private final CrawlingStatusManager crawlingStatusManager;
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> getCrawlingStatus() {
        return ResponseEntity.ok(ApiResponse.ok("크롤링 상태 조회", crawlingStatusManager.isCrawling()));
    }
   } 
