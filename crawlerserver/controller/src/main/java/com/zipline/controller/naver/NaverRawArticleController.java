package com.zipline.controller.naver;



import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.naver.NaverRawArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zipline.global.util.CrawlingStatusUtil.checkAndExecute;

@RestController
@RequestMapping("/api/v1/crawl/naver")
@RequiredArgsConstructor
public class NaverRawArticleController {

	private final NaverRawArticleService naverRawArticleService;
	private final CrawlingStatusManager crawlingStatusManager;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Void>> crawlAllRawArticleFromNaver() {
		return checkAndExecute(crawlingStatusManager, 
			() -> naverRawArticleService.crawlAndSaveRawArticlesByLevel(3), 
			"레벨 " + 3 + " 원본 매물 정보 수집이 시작되었습니다.");
	}

	@GetMapping("/region/{cortarNo}")
	public ResponseEntity<ApiResponse<Void>> crawlRawArticlesByRegion(@PathVariable Long cortarNo) {
		return checkAndExecute(crawlingStatusManager, 
			() -> naverRawArticleService.crawlAndSaveRawArticlesForRegion(cortarNo), 
			"지역 " + cortarNo + " 원본 매물 정보 수집이 시작되었습니다.");
	}
}
