package com.zipline.service.region;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.publicitem.Region;
import com.zipline.global.exception.custom.region.RegionException;
import com.zipline.global.exception.custom.region.errorcode.RegionErrorCode;
import com.zipline.global.response.ApiResponse;
import com.zipline.repository.region.RegionRepository;
import com.zipline.service.region.dto.RegionResponseDTO;
import com.zipline.service.region.dto.RegionResponseDTO.FlatRegionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String LV1_REGIONS_CACHE_KEY = "regions:level:1";
    private static final String CHILDREN_REGIONS_CACHE_KEY_PREFIX = "regions:children:";

    @Override
    public ApiResponse<RegionResponseDTO> getLv1Regions() {
        List<FlatRegionDTO> lv1Regions = getCachedFlatRegions(LV1_REGIONS_CACHE_KEY, () -> {
            List<Region> regions = regionRepository.findByLevel(1);
            return regions.stream()
                    .map(FlatRegionDTO::from)
                    .collect(Collectors.toList());
        });
        if (lv1Regions.isEmpty()) {
            throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
        }
        return ApiResponse.ok("레벨 1 지역 조회 성공", new RegionResponseDTO(lv1Regions));
    }

    @Override
    public ApiResponse<RegionResponseDTO> getChildrenRegions(Long cortaNo) {
        String cacheKey = CHILDREN_REGIONS_CACHE_KEY_PREFIX + cortaNo;
        List<FlatRegionDTO> childrenRegions = getCachedFlatRegions(cacheKey, () -> {
            List<Region> regions = regionRepository.findByParentCortarNo(cortaNo);
            return regions.stream()
                    .map(FlatRegionDTO::from)
                    .collect(Collectors.toList());
        });
        if (childrenRegions.isEmpty()) {
            throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
        }
        return ApiResponse.ok("자식 지역 리스트 조회 성공", new RegionResponseDTO(childrenRegions));
    }

    private List<FlatRegionDTO> getCachedFlatRegions(String cacheKey, RegionSupplier<List<FlatRegionDTO>> regionSupplier) {
        try {
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("캐시키: {}", cacheKey);
                log.debug("캐시 데이터: {}", cachedData);
                try {
                    return objectMapper.readValue(cachedData, new TypeReference<List<FlatRegionDTO>>() {});
                } catch (Exception e) {
                    log.error("디시리얼라이즈 실패: {}", cacheKey, e);
                    redisTemplate.delete(cacheKey);
                    throw new RegionException(RegionErrorCode.CACHE_ACCESS_UNAVAILABLE);
                }
            }
            log.debug("캐시키 없음: {}", cacheKey);
            List<FlatRegionDTO> regions = regionSupplier.get();
            String serializedData = objectMapper.writeValueAsString(regions);
            redisTemplate.opsForValue().set(cacheKey, serializedData, 1, TimeUnit.HOURS);
            log.debug("캐싱된 데이터 키: {}", cacheKey);
            return regions;
        } catch (Exception e) {
            log.error("캐시 접근 에러: {}", cacheKey, e);
            throw new RegionException(RegionErrorCode.CACHE_ACCESS_UNAVAILABLE);
        }
    }

    @FunctionalInterface
    private interface RegionSupplier<T> {
        T get();
    }
}
