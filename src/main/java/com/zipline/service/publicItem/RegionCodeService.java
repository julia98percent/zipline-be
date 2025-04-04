package com.zipline.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.dto.publicItem.RegionDto;
import com.zipline.entity.publicItem.Region;
import com.zipline.repository.publicItem.RegionRepository;
import com.zipline.global.util.RandomSleepUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionCodeService {
    private final RegionRepository regionRepository;
    
    /**
     * 네이버 부동산 API에서 지역 정보를 가져와 데이터베이스에 저장합니다.
     * 계층 구조: 시/도(레벨 1) -> 시/군/구(레벨 2) -> 읍/면/동(레벨 3)
     * 
     * - 시/도: 11 (레벨 1)
     * - 시/군/구: 1168 (레벨 2)한국의 시/도
     * https://new.land.naver.com/api/regions/list?cortarNo=0000000000
     * 
     * ```json
     * "regionList": [
     * {
     * "cortarNo": "1100000000",
     * "centerLat": 37.566427,
     * "centerLon": 126.977872,
     * "cortarName": "서울시",
     * "cortarType": "city"
     * },
     * 
     * ]
     * }
     * ```
     * 
     * 
     * 한국의 시/군/구
     * {서울}안에 모든 동의 cortar no
     * https://new.land.naver.com/api/regions/list?cortarNo=1100000000
     * 
     * ```json
     * {
     * "regionList": [
     * {
     * "cortarNo": "1168000000",
     * "centerLat": 37.517408,
     * "centerLon": 127.047313,
     * "cortarName": "강남구",
     * "cortarType": "dvsn"
     * },
     * 
     * 
     * ......
     * 
     * ]
     * }
     * 
     * ```
     * 
     * 
     * https://new.land.naver.com/api/regions/list?cortarNo=1168000000
     * 
     * 
     * 한국의 읍/면/동 강남구
     * ```json
     * 
     * {
     * "regionList": [
     * {
     * "cortarNo": "1168010300",
     * "centerLat": 37.482968,
     * "centerLon": 127.0634,
     * "cortarName": "개포동",
     * "cortarType": "sec"
     * },
     * ......
     * ]
     * }
     * - 읍/면/동: 11680103 (레벨 3)
     */

    public void crawlAndSaveRegions() {
        log.info("지역 정보 수집 시작");
        
        // 시/도 데이터 수집
        log.info("시/도 데이터 수집 시작");
        String apiUrl = "https://new.land.naver.com/api/regions/list?cortarNo=0000000000";
        log.info("API 요청 URL: {}", apiUrl);
        
        try {
            String response = getRegions(apiUrl);
            log.info("API 응답: {}", response);
            
            if (response != null && !response.isEmpty()) {
                List<RegionDto> regions = parseRegions(response);
                log.info("파싱된 지역 정보: {}", regions);
                
                for (RegionDto region : regions) {
                    saveRegion(region, 1);
                }
            } else {
                log.error("지역 목록이 비어있습니다.");
            }
        } catch (Exception e) {
            log.error("시/도 데이터 수집 중 오류 발생: {}", e.getMessage());
        }
        
        // 시/군/구 데이터 수집
        log.info("시/군/구 데이터 수집 시작");
        List<Region> cities = regionRepository.findByLevel(1);
        log.info("수집된 시/도 수: {}", cities.size());
        
        for (Region city : cities) {
            RandomSleepUtil.sleepShort(); // API 요청 전 대기
            
            apiUrl = "https://new.land.naver.com/api/regions/list?cortarNo=" + city.getCortarNo();
            log.info("API 요청 URL: {}", apiUrl);
            
            try {
                String response = getRegions(apiUrl);
                log.info("API 응답: {}", response);
                
                if (response != null && !response.isEmpty()) {
                    List<RegionDto> regions = parseRegions(response);
                    log.info("파싱된 지역 정보: {}", regions);
                    
                    for (RegionDto region : regions) {
                        saveRegion(region, 2);
                    }
                } else {
                    log.error("지역 목록이 비어있습니다.");
                }
            } catch (Exception e) {
                log.error("시/군/구 데이터 수집 중 오류 발생: {}", e.getMessage());
            }
        }
        
        // 읍/면/동 데이터 수집
        log.info("읍/면/동 데이터 수집 시작");
        List<Region> districts = regionRepository.findByLevel(2);
        log.info("수집된 시/군/구 수: {}", districts.size());
        
        for (Region district : districts) {
            RandomSleepUtil.sleepShort(); // API 요청 전 대기
            
            apiUrl = "https://new.land.naver.com/api/regions/list?cortarNo=" + district.getCortarNo();
            log.info("API 요청 URL: {}", apiUrl);
            
            try {
                String response = getRegions(apiUrl);
                log.info("API 응답: {}", response);
                
                if (response != null && !response.isEmpty()) {
                    List<RegionDto> regions = parseRegions(response);
                    log.info("파싱된 지역 정보: {}", regions);
                    
                    for (RegionDto region : regions) {
                        saveRegion(region, 3);
                    }
                } else {
                    log.error("지역 목록이 비어있습니다.");
                }
            } catch (Exception e) {
                log.error("읍/면/동 데이터 수집 중 오류 발생: {}", e.getMessage());
            }
        }
        
        // 최종 데이터베이스 상태 확인
        log.info("\n=== 최종 데이터베이스 상태 ===");
        List<Region> allRegions = regionRepository.findAll();
        log.info("전체 지역 수: {}", allRegions.size());
        
        List<Region> level1Regions = regionRepository.findByLevel(1);
        List<Region> level2Regions = regionRepository.findByLevel(2);
        List<Region> level3Regions = regionRepository.findByLevel(3);
        
        log.info("시/도 수: {}", level1Regions.size());
        log.info("시/군/구 수: {}", level2Regions.size());
        log.info("읍/면/동 수: {}", level3Regions.size());
        
        log.info("지역 정보 수집 완료");
    }
    
    /**
     * 지역 정보를 데이터베이스에 저장합니다.
     * 이미 존재하는 경우 정보를 업데이트합니다.
     */
    private void saveRegion(RegionDto dto, int level) {
        log.info("\n=== 지역 정보 저장 시작 ===");
        log.info("저장할 지역: {} (레벨: {})", dto.getCortarName(), level);
          
        Optional<Region> existingRegion = regionRepository.findByCortarNo(dto.getCortarNo());
        
        Region region;
        if (existingRegion.isPresent()) {
            region = existingRegion.get();
            log.info("기존 지역 정보 업데이트: {}", region.getCortarName());
        } else {
            region = new Region();
            region.setCortarNo(dto.getCortarNo());
            region.setCortarName(dto.getCortarName());
            region.setLevel(level);
            region.setNaverStatus(Region.CrawlStatus.NEW);
            region.setZigbangStatus(Region.CrawlStatus.NEW); 
            region.setDabangStatus(Region.CrawlStatus.NEW); 
            log.info("새로운 지역 정보 생성: {}", region.getCortarName());
        }
        
        region.setCenterLat(dto.getCenterLat());
        region.setCenterLon(dto.getCenterLon());
        
        Region savedRegion = regionRepository.save(region);
        log.info("저장된 지역 정보: cortarNo={}, cortarName={}, level={}, centerLat={}, centerLon={}",
            savedRegion.getCortarNo(),
            savedRegion.getCortarName(),
            savedRegion.getLevel(),
            savedRegion.getCenterLat(),
            savedRegion.getCenterLon());
        log.info("=== 지역 정보 저장 완료 ===\n");
    }
    
    /**
     * 네이버 부동산 API에서 지역 정보를 가져옵니다.
     */
    private String getRegions(String apiUrl) {
        try {
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            conn.setRequestProperty("Host", "new.land.naver.com");
            conn.setRequestProperty("Referer", "https://new.land.naver.com/");
            conn.setRequestProperty("sec-ch-ua",
                    "\"Chromium\";v=\"122\", \"Not(A:Brand\";v=\"24\", \"Google Chrome\";v=\"122\"");
            conn.setRequestProperty("sec-ch-ua-mobile", "?0");
            conn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            conn.setRequestProperty("Sec-Fetch-Dest", "empty");
            conn.setRequestProperty("Sec-Fetch-Mode", "cors");
            conn.setRequestProperty("Sec-Fetch-Site", "same-origin");

            int responseCode = conn.getResponseCode();
            log.info("응답 코드: {}", responseCode);

            if (responseCode == 200) {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                log.error("API 요청 실패. 응답 코드: {}", responseCode);
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    log.error("에러 응답: {}", errorResponse.toString());
                }
                return null;
            }
        } catch (Exception e) {
            log.error("API 요청 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * JSON 응답을 파싱하여 RegionDto 리스트로 변환합니다.
     * 
     * @param jsonResponse 파싱할 JSON 문자열
     * @return RegionDto 리스트
     */
    public List<RegionDto> parseRegions(String jsonResponse) {
        List<RegionDto> regions = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode regionsNode = root.path("regionList");

            if (regionsNode.isArray()) {
                for (JsonNode regionNode : regionsNode) {
                    RegionDto region = RegionDto.builder()
                            .cortarNo(regionNode.path("cortarNo").asLong())
                            .cortarName(regionNode.path("cortarName").asText())
                            .centerLat(regionNode.path("centerLat").asDouble())
                            .centerLon(regionNode.path("centerLon").asDouble())
                            .build();
                    regions.add(region);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("지역 정보 파싱 중 오류 발생: " + e.getMessage(), e);
        }
        return regions;
    }
}
