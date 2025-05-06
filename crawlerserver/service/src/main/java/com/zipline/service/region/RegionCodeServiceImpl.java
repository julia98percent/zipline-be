package com.zipline.service.region;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.Task;
import com.zipline.global.task.TaskManager;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.service.region.dto.RegionDTO;
import com.zipline.domain.entity.region.Region;
import com.zipline.global.util.RandomSleepUtil;
import com.zipline.infrastructure.region.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class RegionCodeServiceImpl implements RegionCodeService {
    private final TaskManager taskManager;
    private final TaskExecutor taskExecutor;
    private final RegionRepository regionRepository;
    private static final String API_BASE_URL = "https://new.land.naver.com/api/regions/list?cortarNo=";
    private static final Long KOREA_CORTAR_NO = 0L;

    public TaskResponseDto crawlAndSaveRegions() {
        // First check if task is already running
        if (taskManager.isTaskRunning(TaskType.NAVERCRAWLING)) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
        }

        // Create task and immediately get a reference to it
        final Task task = taskManager.createTask(TaskType.NAVERCRAWLING);

        // Start async processing
        CompletableFuture.runAsync(() -> {
            try {
                // Main crawler logic
                executeCrawlAndSaveRegions(task);

                // Mark as successful upon completion
                task.markAsCompleted();
                taskManager.updateTaskStatus(TaskType.NAVERCRAWLING, TaskStatus.COMPLETED);
                log.info("지역 정보 수집 완료됨");
            } catch (Exception e) {
                log.error("테스크 실행중 오류 발생 {}", e.getMessage(), e);
                task.markAsFailed(e.getMessage());
                taskManager.updateTaskStatus(TaskType.NAVERCRAWLING, TaskStatus.FAILED);
            }
        }, taskExecutor);

        return TaskResponseDto.fromTask(task);
    }

    private void executeCrawlAndSaveRegions(Task task) {
        log.info("지역 정보 수집 시작");
        initializeKoreaIfNotExists();
        collectRegionsForLevel(1);
        collectRegionsForLevel(2);
        collectRegionsForLevel(3);
    }

    @Transactional
    private void initializeKoreaIfNotExists() {
        if (regionRepository.findByCortarNo(KOREA_CORTAR_NO).isEmpty()) {
            Region koreaRegion = RegionDTO.createKoreaRegion();
            regionRepository.save(koreaRegion);
        }
    }

    private String formatCortarNo(Long cortarNo) {
        return String.format("%010d", cortarNo);
    }

    private void collectRegionsForParent(Long parentCortarNo, int targetLevel) {
        String formattedCortarNo = formatCortarNo(parentCortarNo);
        String response = fetchRegionsFromApi(API_BASE_URL + formattedCortarNo);
        if (response != null) {
            List<RegionDTO> regions = parseRegions(response);
            regions.forEach(region -> saveRegion(parentCortarNo, region, targetLevel));
        }
    }

    @Transactional
    private void collectRegionsForLevel(int targetLevel) {
        List<Region> parents = regionRepository.findByLevel(targetLevel - 1);
        int total = parents.size();
        int count = 0;

        for (Region parent : parents) {
            count++;
            // Log progress periodically for visibility
            if (count % 10 == 0 || count == total) {
                log.info("수집 중: 레벨 {} - {} / {} 완료", targetLevel, count, total);
            }

            try {
                RandomSleepUtil.sleepShort();
                collectRegionsForParent(parent.getCortarNo(), targetLevel);
            } catch (Exception e) {
                // Log error but continue with next item
                log.error("Region {} processing failed: {}", parent.getCortarNo(), e.getMessage());
            }
        }
    }

    private String fetchRegionsFromApi(String apiUrl) {
        HttpURLConnection conn = null;
        try {
            conn = createConnection(apiUrl);
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
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpURLConnection createConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000); // 10 seconds
        conn.setReadTimeout(15000);    // 15 seconds
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

    @Transactional
    private void saveRegion(Long parentCortarNo, RegionDTO dto, int level) {
        try {
            Region parentRegion = parentCortarNo == null ?
                    null :
                    regionRepository.findByCortarNo(parentCortarNo)
                            .orElseThrow(() -> new IllegalArgumentException("부모지역 미발견: " + parentCortarNo));
            Region region = dto.toEntity(level, parentRegion);
            regionRepository.save(region);
        } catch (Exception e) {
            log.error("지역 저장 중 오류: {} - {}", dto.getCortarName(), e.getMessage());
        }
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
            log.error("지역 정보 파싱 중 오류 발생: {}", e.getMessage());
            // Return what we have so far instead of throwing
            return regions;
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
