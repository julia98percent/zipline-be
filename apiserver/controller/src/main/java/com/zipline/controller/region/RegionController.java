package com.zipline.controller.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.region.RegionService;
import com.zipline.service.region.dto.FlatRegionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/test/region")
@Tag(name = "지역 api", description = "지역 조회 api")
@RestController
public class RegionController{

    private final RegionService regionService;

    @Operation(summary = "자식 지역을 전체 조회합니다", description = "자식 지역을 전체 조회합니다.")
    @GetMapping("/{cortaNo}")
    public ResponseEntity<ApiResponse<List<FlatRegionDTO>>> getBranchRegions(@PathVariable Long cortaNo) {
        ApiResponse<List<FlatRegionDTO>> response = regionService.getChildrenRegions(cortaNo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
