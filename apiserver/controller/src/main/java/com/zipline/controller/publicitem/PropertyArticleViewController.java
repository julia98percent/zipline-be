package com.zipline.controller.publicitem;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.publicitem.PropertyArticleViewService;
import com.zipline.service.publicitem.dto.PropertyArticlePageResponseDTO;
import com.zipline.service.publicitem.dto.PropertyArticleSearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/property-articles")
@RequiredArgsConstructor
@Tag(name = "공개 매물 목록 검색", description = "공개 매물 목록 검색 API")
public class PropertyArticleViewController {

	private final PropertyArticleViewService propertyArticleViewService;

	@Operation(summary = "매물 목록 검색", description = "매물 목록 검색 API")
	@GetMapping("/search")
	public ResponseEntity<PropertyArticlePageResponseDTO> searchPropertyArticles(
			@ModelAttribute PageRequestDTO pageRequestDTO,
			@ModelAttribute PropertyArticleSearchDTO searchDTO) {
		PropertyArticlePageResponseDTO response = propertyArticleViewService.searchPropertyArticles(pageRequestDTO, searchDTO);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "지역별 매물 목록 조회", description = "지역별 매물 목록 조회 API")
	@GetMapping("/region/{regionCode}")
	public ResponseEntity<PropertyArticlePageResponseDTO> getPropertyArticlesByRegion(
			@ModelAttribute PageRequestDTO pageRequestDTO,
			@PathVariable String regionCode) {
		PropertyArticlePageResponseDTO response = propertyArticleViewService.getPropertyArticlesByRegion(pageRequestDTO, regionCode);
		return ResponseEntity.ok(response);
	}
}