package com.zipline.controller.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.region.RegionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zipline.global.util.CrawlingStatusUtil.checkAndExecute;

@RestController
@RequestMapping("/api/v1/crawl/region")
@RequiredArgsConstructor
public class RegionCrawlController {

	private final RegionCodeService regionCodeService;
	private final CrawlingStatusManager crawlingStatusManager;

	@GetMapping
	public ResponseEntity<ApiResponse<Void>> crawlRegions() {
		return checkAndExecute(crawlingStatusManager, 
			() -> regionCodeService.crawlAndSaveRegions(), 
			"지역 정보 수집이 시작되었습니다.");
	}
}
