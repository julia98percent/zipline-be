
# `StackOverflowError` 및 Redis 캐싱 문제 해결 단계별 문서

이 문서는 `Region` 엔티티의 순환 참조로 인해 발생한 `StackOverflowError` 문제와 Redis 캐싱 및 역직렬화 문제를 단계별로 분석하고 해결한 과정을 설명합니다.

---

## 1. 문제 식별
### **발생한 문제**
1. `Region` 엔티티를 직렬화하는 과정에서 `StackOverflowError`가 발생.
2. Redis에 캐싱된 데이터를 역직렬화하는 과정에서 `InvalidDefinitionException`이 발생.

### **에러 메시지**
#### 순환 참조 문제:
```
Caused by: com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError)
(through reference chain: com.zipline.entity.publicitem.Region["parent"]->com.zipline.entity.publicitem.Region["children"]->...)
```

#### 역직렬화 문제:
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.zipline.service.region.dto.RegionResponseDTO$FlatRegionDTO` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
```

---

## 2. 원인 분석
### **주요 관찰 사항**
1. `Region` 엔티티는 자기 참조 관계를 가짐:
   - `parent` (Many-to-One 관계).
   - `children` (One-to-Many 관계).
   - Jackson 직렬화가 `parent`와 `children`을 직렬화하려고 시도하면서 무한 재귀가 발생.
2. Redis에 저장된 JSON 데이터를 역직렬화할 때, `FlatRegionDTO` 클래스에 기본 생성자가 없어서 Jackson이 객체를 생성하지 못함.

---

## 3. 초기 해결 시도
### **1단계: 순환 참조 방지를 위한 `@JsonIgnore` 추가**
- `parent`와 `children` 필드에 `@JsonIgnore`를 추가하여 Jackson이 해당 필드를 직렬화하지 않도록 설정.

### **수정된 엔티티**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JsonIgnore
private Region parent;

@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnore
private List<Region> children;
```

### **결과**
- 무한 재귀 문제는 해결되었으나, `parent`와 `children` 필드가 응답에서 제외되어 애플리케이션 요구사항을 충족하지 못함.

---

## 4. 개선된 해결책
### **2단계: `Region` 엔티티를 평탄화하여 직렬화**
- `FlatRegionDTO` 클래스를 도입하여 `Region` 엔티티의 평탄화된 버전을 표현.
- `FlatRegionDTO`는 필요한 필드만 포함하며, 중첩된 관계를 제거.

### **DTO 정의**
```java
@Getter
@Builder
public static class FlatRegionDTO {
    private Long cortarNo;
    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level;
    private Long parentCortarNo;

    public static FlatRegionDTO from(Region region) {
        return FlatRegionDTO.builder()
                .cortarNo(region.getCortarNo())
                .cortarName(region.getCortarName())
                .centerLat(region.getCenterLat())
                .centerLon(region.getCenterLon())
                .level(region.getLevel())
                .parentCortarNo(region.getParent() != null ? region.getParent().getCortarNo() : null)
                .build();
    }
}
```

### **결과**
- `Region` 엔티티를 평탄화하여 필요한 필드만 직렬화함으로써 순환 참조 문제를 방지.

---

## 5. Redis 역직렬화 문제 해결
### **3단계: `FlatRegionDTO`에 `@JsonCreator` 추가**
- Jackson이 `FlatRegionDTO`를 역직렬화할 수 있도록 생성자를 추가.

### **수정된 DTO**
```java
@Getter
@Builder
public static class FlatRegionDTO {
    private Long cortarNo;
    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level;
    private Long parentCortarNo;

    @JsonCreator
    public FlatRegionDTO(
            @JsonProperty("cortarNo") Long cortarNo,
            @JsonProperty("cortarName") String cortarName,
            @JsonProperty("centerLat") Double centerLat,
            @JsonProperty("centerLon") Double centerLon,
            @JsonProperty("level") Integer level,
            @JsonProperty("parentCortarNo") Long parentCortarNo) {
        this.cortarNo = cortarNo;
        this.cortarName = cortarName;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.level = level;
        this.parentCortarNo = parentCortarNo;
    }
}
```

### **결과**
- Redis에 저장된 JSON 데이터를 `FlatRegionDTO`로 역직렬화할 수 있게 됨.

---

## 6. Redis 캐싱 로직 개선
### **4단계: 캐싱 로직에서 역직렬화 실패 시 캐시 삭제**
- Redis에 저장된 데이터가 손상되었을 경우, 해당 키를 삭제하여 문제를 방지.

### **수정된 서비스 로직**
```java
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
                redisTemplate.delete(cacheKey); // 손상된 캐시 삭제
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
```

### **결과**
- Redis에 손상된 데이터가 있을 경우, 자동으로 삭제하여 문제를 방지.

---

## 7. 테스트 및 검증
### **4단계: API 엔드포인트 테스트**
- `getLv1Regions` 및 `getChildrenRegions` 엔드포인트를 테스트하여 평탄화된 데이터가 올바르게 반환되는지 확인.
- Redis 캐싱 및 역직렬화가 정상적으로 작동하는지 확인.

### **5단계: Redis 데이터 확인**
- `redis-cli`를 사용하여 Redis에 저장된 데이터를 확인하고 평탄화된 형식으로 저장되었는지 검증.

---

## 8. 최종 코드 구조
### **RegionServiceImpl**
```java
@Override
public ApiResponse<RegionResponseDTO> getLv1Regions() {
    List<FlatRegionDTO> lv1Regions = getCachedFlatRegions(LV1_REGIONS_CACHE_KEY, () -> {
        List<Region> regions = regionRepository.findByLevel(1);
        return regions.stream()
                .map(FlatRegionDTO::from)
                .collect(Collectors.toList());
    });
    return ApiResponse.ok("레벨 1 지역 조회 성공", new RegionResponseDTO(lv1Regions));
}
```

---

## 9. 교훈
1. **순환 참조 문제**:
   - 자기 참조 엔티티는 직렬화 시 문제가 발생할 수 있으므로 DTO를 사용하여 해결.
2. **Redis 캐싱**:
   - 캐싱된 데이터가 손상될 가능성을 고려하여 복구 로직을 추가.
3. **테스트 중요성**:
   - API 테스트와 Redis 데이터 검증을 통해 문제 해결 여부를 확인.

---
