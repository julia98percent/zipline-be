package com.zipline.controller.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.service.region.RegionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/crawl/region")
@RequiredArgsConstructor
public class RegionCrawlController {

	private final RegionCodeService regionCodeService;

	@GetMapping
	public ResponseEntity<ApiResponse<TaskResponseDto>> crawlRegions() {
		TaskResponseDto result = regionCodeService.crawlAndSaveRegions();
		ApiResponse<TaskResponseDto> response = ApiResponse.ok("지역 정보 수집 시작", result);
		return ResponseEntity.ok(response);
	}
}