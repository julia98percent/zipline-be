package com.zipline.controller.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.region.RegionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/crawl/region")
@RequiredArgsConstructor
public class RegionCrawlController {

	private final RegionCodeService regionCodeService;

	@GetMapping
	public ResponseEntity<ApiResponse<TaskResponseDto>> crawlRegions(
			@RequestParam(defaultValue = "false") Boolean useProxy
	) {
		TaskResponseDto result = regionCodeService.crawlAndSaveRegions(useProxy);
		ApiResponse<TaskResponseDto> response = ApiResponse.ok("지역 정보 수집 시작", result);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/region/{cortarNo}")
	public ResponseEntity<ApiResponse<TaskResponseDto>> crawlRegionsForRegion(
			@PathVariable Long cortarNo,
			@RequestParam(defaultValue = "false") Boolean useProxy
	){
		TaskResponseDto result = regionCodeService.crawlAndSaveRegionsForRegion(useProxy, cortarNo);
		ApiResponse<TaskResponseDto> response = ApiResponse.ok("지역" + cortarNo + "의 하위 지역 정보 수집 시작", result);
		return ResponseEntity.ok(response);
	}
}