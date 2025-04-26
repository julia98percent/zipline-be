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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cache.keys.children-regions-prefix}")
    private String childrenRegionsCacheKeyPrefix;

    @Override
    public ApiResponse<RegionResponseDTO> getChildrenRegions(Long cortaNo) {
        String cacheKey = childrenRegionsCacheKeyPrefix + cortaNo;
        List<FlatRegionDTO> childrenRegions = getCachedFlatRegions(cacheKey,
                () -> regionRepository.findByParentCortarNo(cortaNo).stream()
                        .map(FlatRegionDTO::from)
                        .collect(Collectors.toList())
        );
        if (childrenRegions.isEmpty()) {
            throw new RegionException(RegionErrorCode.REGION_NOT_FOUND);
        }
        return ApiResponse.ok("자식 지역 리스트 조회 성공", new RegionResponseDTO(childrenRegions));
    }

    private List<FlatRegionDTO> getCachedFlatRegions(String cacheKey, RegionSupplier<List<FlatRegionDTO>> regionSupplier) {
        List<FlatRegionDTO> cachedRegions = getFromCache(cacheKey);
        if (cachedRegions != null) return cachedRegions;
        List<FlatRegionDTO> regions = getFromDatabase(regionSupplier);
        cacheData(cacheKey, regions);
        return regions;
    }

    /**
     * 캐시에서 데이터를 조회합니다.
     */
    private List<FlatRegionDTO> getFromCache(String cacheKey) {
        String cachedData = getDataFromRedis(cacheKey);
        if (cachedData == null) return null;
        return deserializeData(cachedData, cacheKey);
    }

    private String getDataFromRedis(String cacheKey) {
        try {
            return redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("Redis 캐시 접근 실패: {}", cacheKey, e);
            return null;
        }
    }

    private List<FlatRegionDTO> deserializeData(String cachedData, String cacheKey) {
        try {
            return objectMapper.readValue(cachedData, new TypeReference<List<FlatRegionDTO>>() {});
        } catch (Exception e) {
            log.warn("캐시 데이터 역직렬화 실패: {}", cacheKey, e);
            return null;
        }
    }

    /**
     * 데이터베이스에서 데이터를 조회합니다.
     */
    private List<FlatRegionDTO> getFromDatabase(RegionSupplier<List<FlatRegionDTO>> regionSupplier) {
        try {
            return regionSupplier.get();
        } catch (Exception e) {
            log.error("데이터베이스 조회 실패", e);
            throw new RegionException(RegionErrorCode.CACHE_ACCESS_UNAVAILABLE);
        }
    }

    /**
     * 데이터를 캐시에 저장합니다.
     */
    private void cacheData(String cacheKey, List<FlatRegionDTO> data) {
        if (data == null) return;
        try {
            String serializedData = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(cacheKey, serializedData, 60, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis 캐싱 실패: {}", cacheKey, e);
        }
    }

    @FunctionalInterface
    private interface RegionSupplier<T> {
        T get();
    }
}
