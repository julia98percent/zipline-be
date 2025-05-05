package com.zipline.controller.naver;


import com.zipline.service.proxy.dto.ProxyStatusDTO;
import com.zipline.global.response.ApiResponse;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.infrastructure.proxy.ProxyPool;
import com.zipline.service.naver.ProxyNaverRawArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zipline.global.util.CrawlingStatusUtil.checkAndExecute;

@RestController
@RequestMapping("/api/v1/crawl/naver-proxy")
@RequiredArgsConstructor
public class NaverProxyRawArticleController {

	private final ProxyNaverRawArticleService proxyNaverRawArticleService;
	private final CrawlingStatusManager crawlingStatusManager;
	private final ProxyPool proxyPool;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Void>> crawlAllRawArticleFromNaverWithProxy() {
		return checkAndExecute(crawlingStatusManager,
			() -> proxyNaverRawArticleService.crawlAndSaveRawArticles(),
			"프록시를 통한 레벨 원본 매물 정보 수집이 시작되었습니다.");
	}

	@GetMapping("/region/{cortarNo}")
	public ResponseEntity<ApiResponse<Void>> crawlRawArticlesWithProxyByRegion(@PathVariable Long cortarNo) {
		return checkAndExecute(crawlingStatusManager,
			() -> proxyNaverRawArticleService.crawlAndSaveRawArticlesForRegion(cortarNo),
			"프록시를 통한 지역 " + cortarNo + " 원본 매물 정보 수집이 시작되었습니다.");
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
