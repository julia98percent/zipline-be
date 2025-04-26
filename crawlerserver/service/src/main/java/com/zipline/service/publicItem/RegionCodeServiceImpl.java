package com.zipline.service.publicItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.dto.publicitem.RegionDTO;
import com.zipline.domain.entity.enums.CrawlStatus;
import com.zipline.domain.entity.publicitem.Region;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.infrastructure.publicItem.repository.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionCodeServiceImpl implements RegionCodeService {
    private final RegionRepository regionRepository;
    private static final String API_BASE_URL = "https://new.land.naver.com/api/regions/list?cortarNo=";
    private static final Long KOREA_CORTAR_NO = 0000000000L;

    public void crawlAndSaveRegions() {
        log.info("지역 정보 수집 시작");
        initializeKoreaIfNotExists();
        collectRegionsForLevel(KOREA_CORTAR_NO, 1);
        collectRegionsForParents(1, 2);
        collectRegionsForParents(2, 3);
        logCollectionSummary();
    }

    private void initializeKoreaIfNotExists() {
        if (regionRepository.findByCortarNo(KOREA_CORTAR_NO).isEmpty()) {
            Region koreaRegion = RegionDTO.createKoreaRegion();
            regionRepository.save(koreaRegion);
        }
    }

    private void collectRegionsForLevel(Long parentCortarNo, int targetLevel) {
        String response = fetchRegionsFromApi(API_BASE_URL + parentCortarNo);
        if (response != null) {
            List<RegionDTO> regions = parseRegions(response);
            regions.forEach(region -> saveRegion(parentCortarNo, region, targetLevel));
        }
    }

    private void collectRegionsForParents(int parentLevel, int targetLevel) {
        List<Region> parents = regionRepository.findByLevel(parentLevel);
        for (Region parent : parents) {
            RandomSleepUtil.sleepShort();
            collectRegionsForLevel(parent.getCortarNo(), targetLevel);
        }
    }

    private String fetchRegionsFromApi(String apiUrl) {
        try {
            HttpURLConnection conn = createConnection(apiUrl);
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                return readResponse(conn.getInputStream());
            } else {
                log.error("API 요청 실패. 응답 코드: {}", responseCode);
                return null;
            }
        } catch (Exception e) {
            log.error("API 요청 중 오류: {}", e.getMessage());
            return null;
        }
    }

    private HttpURLConnection createConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

        return conn;
    }

    private String readResponse(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void saveRegion(Long parentCortarNo, RegionDTO dto, int level) {
        Region parentRegion = parentCortarNo > 0 ?
            regionRepository.findByCortarNo(parentCortarNo)
                .orElseThrow(() -> new IllegalArgumentException("Parent region not found: " + parentCortarNo))
            : null;

        Region region = dto.toEntity(level, parentRegion);
        regionRepository.save(region);
    }

    public List<RegionDTO> parseRegions(String jsonResponse) {
        List<RegionDTO> regions = new ArrayList<>();
        try {
            JsonNode regionsNode = new ObjectMapper()
                .readTree(jsonResponse)
                .path("regionList");

            if (regionsNode.isArray()) {
                for (JsonNode regionNode : regionsNode) {
                    regions.add(RegionDTO.fromJsonNode(regionNode));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("지역 정보 파싱 중 오류 발생: " + e.getMessage(), e);
        }
        return regions;
    }

    private void logCollectionSummary() {
        List<Region> allRegions = regionRepository.findAll();
        log.info("수집 완료 - 전체: {}, 시/도: {}, 시/군/구: {}, 읍/면/동: {}",
            allRegions.size(),
            regionRepository.findByLevel(1).size(),
            regionRepository.findByLevel(2).size(),
            regionRepository.findByLevel(3).size()
        );
    }
}


