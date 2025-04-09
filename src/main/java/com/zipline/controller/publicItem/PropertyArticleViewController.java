package com.zipline.controller.publicItem;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zipline.dto.publicItem.PropertyArticlePageResponseDTO;
import com.zipline.dto.publicItem.PropertyArticleSearchDTO;
import com.zipline.dto.publicItem.PropertyArticleViewDTO;
import com.zipline.entity.enums.Category;
import com.zipline.service.publicItem.PropertyArticleViewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/property-articles")
@RequiredArgsConstructor
public class PropertyArticleViewController {

    private final PropertyArticleViewService propertyArticleViewService;

    /**
     * 매물 목록 검색 API
     * 다양한 조건으로 매물을 검색하고 정렬할 수 있습니다.
     */
    @GetMapping("/search")
    public ResponseEntity<PropertyArticlePageResponseDTO> searchPropertyArticles(
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String buildingType,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Long minDeposit,
            @RequestParam(required = false) Long maxDeposit,
            @RequestParam(required = false) Long minMonthlyRent,
            @RequestParam(required = false) Long maxMonthlyRent,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PropertyArticleSearchDTO searchDTO = PropertyArticleSearchDTO.builder()
                .regionCode(regionCode)
                .buildingName(buildingName)
                .buildingType(buildingType)
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minDeposit(minDeposit)
                .maxDeposit(maxDeposit)
                .minMonthlyRent(minMonthlyRent)
                .maxMonthlyRent(maxMonthlyRent)
                .minArea(minArea)
                .maxArea(maxArea)
                .startDate(startDate)
                .endDate(endDate)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        PropertyArticlePageResponseDTO response = propertyArticleViewService.searchPropertyArticles(searchDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 지역별 매물 목록 조회 API
     */
    @GetMapping("/region/{regionCode}")
    public ResponseEntity<PropertyArticlePageResponseDTO> getPropertyArticlesByRegion(
            @PathVariable String regionCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PropertyArticlePageResponseDTO response = propertyArticleViewService.getPropertyArticlesByRegion(regionCode, pageable);
        return ResponseEntity.ok(response);
    }
}

