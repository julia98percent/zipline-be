package com.zipline.controller.naver;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.global.util.CrawlingStatusManager;
import com.zipline.service.naver.NaverRawArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crawl/naver")
@RequiredArgsConstructor
public class NaverRawArticleController {

	private final NaverRawArticleService naverRawArticleService;
	private final CrawlingStatusManager crawlingStatusManager;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<TaskResponseDto>> crawlAllRawArticleFromNaver(
			@RequestParam(defaultValue = "false") Boolean useProxy) {
		TaskResponseDto result = naverRawArticleService.crawlAndSaveRawArticles(useProxy);
		ApiResponse<TaskResponseDto> response = ApiResponse.ok("원본 매물 정보 수집이 시작되었습니다.", result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/region/{cortarNo}")
	public ResponseEntity<ApiResponse<TaskResponseDto>> crawlRawArticlesByRegion(
			@PathVariable Long cortarNo,
			@RequestParam(defaultValue = "false") Boolean useProxy) {
    	TaskResponseDto result = naverRawArticleService.crawlAndSaveRawArticlesForRegion(useProxy,cortarNo);
    	ApiResponse<TaskResponseDto> response = ApiResponse.ok("지역 " + cortarNo + " 원본 매물 정보 수집이 시작되었습니다.", result);
    	return ResponseEntity.ok(response);
	}
}
