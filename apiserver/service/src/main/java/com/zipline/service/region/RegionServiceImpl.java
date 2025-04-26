package com.zipline.service.region;

import com.zipline.global.exception.custom.region.RegionException;
import com.zipline.global.exception.custom.region.errorcode.RegionErrorCode;
import com.zipline.global.response.ApiResponse;
import com.zipline.repository.region.RegionRepository;
import com.zipline.service.region.dto.FlatRegionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

        @Override
        @Cacheable(value = "regions", key = "'children:' + #cortaNo", unless = "#result.data.isEmpty()")
        public ApiResponse<List<FlatRegionDTO>> getChildrenRegions(Long cortaNo) {
            List<FlatRegionDTO> childrenRegions = regionRepository.findByParentCortarNo(cortaNo)
                    .stream()
                    .map(FlatRegionDTO::from)
                    .collect(Collectors.toList());
            if (childrenRegions.isEmpty()) {
                throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
            }
            return ApiResponse.ok("자식 지역 리스트 조회 성공", childrenRegions);
        }
}