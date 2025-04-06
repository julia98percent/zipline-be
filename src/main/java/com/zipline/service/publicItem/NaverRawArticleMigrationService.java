package com.zipline.service.publicItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.dto.publicItem.MigrationStatisticsDTO;
import com.zipline.entity.publicItem.NaverRawArticle;
import com.zipline.entity.publicItem.PropertyArticle;
import com.zipline.entity.publicItem.enums.MigrationStatus;
import com.zipline.repository.publicItem.NaverRawArticleRepository;
import com.zipline.repository.publicItem.PropertyArticleRepository;
import com.zipline.repository.publicItem.RegionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 네이버 원본 매물 데이터를 정제하여 PropertyArticle로 마이그레이션하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NaverRawArticleMigrationService {
    private final NaverRawArticleRepository naverRawArticleRepository;
    private final PropertyArticleRepository propertyArticleRepository;
    private final RegionRepository regionRepository;
    private final ObjectMapper objectMapper;
    
    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수

    // /**
    //  * 마이그레이션이 필요한 모든 원본 매물 데이터를 처리합니다.
    //  * 스케줄링된 작업으로 실행됩니다.
    //  */

    public void NaverMigration() {
        log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 시작 ===");
        try {
            // 1. 모든 지역 코드 조회
            List<Long> allRegionCodes = regionRepository.findAllCortarNos();
            log.info("총 {} 개 지역에 대한 마이그레이션 시작", allRegionCodes.size());
            int totalRegions = allRegionCodes.size();
            int processedRegions = 0;
            
            // 2. 각 지역별로 마이그레이션 수행
            for (Long cortarNo : allRegionCodes) {
                try {
                    log.info("지역 {} 마이그레이션 시작 ({}/{})", 
                        cortarNo, ++processedRegions, totalRegions);
                    
                    // 지역별 마이그레이션 수행
                    migrateAllArticlesForRegion(cortarNo);
                    log.info("지역 {} 마이그레이션 완료", cortarNo);
                } catch (Exception e) {
                    log.error("지역 {} 마이그레이션 중 오류 발생: {}", cortarNo, e.getMessage());
                }
            }
            
            // 3. 실패한 마이그레이션 재시도
            retryFailedMigrations();
            log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 완료 ===");
        } catch (Exception e) {
            log.error("마이그레이션 작업 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 지역의 모든 원본 매물 데이터를 마이그레이션합니다.
     *
     * @param cortarNo 지역 코드
     */
    public void migrateAllArticlesForRegion(Long cortarNo) {
        log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 시작", cortarNo);
        try {
            // 해당 지역의 PENDING 상태 매물 수 확인
            long pendingCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.PENDING);
                
            if (pendingCount == 0) {
                log.info("지역 코드 {} 마이그레이션할 데이터가 없습니다", cortarNo);
                return;
            }
            
            log.info("지역 코드 {} 마이그레이션 대상 매물 수: {}", cortarNo, pendingCount);
            int totalProcessed = 0;
            int totalSuccess = 0;
            int totalFailed = 0;
            int pageNumber = (int) Math.ceil((double) pendingCount / BATCH_SIZE);
            boolean hasMoreData = true;
            
            // 페이징 처리로 모든 데이터 마이그레이션
            while (hasMoreData) {
                PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
                Page<NaverRawArticle> pendingArticles = naverRawArticleRepository
                    .findByCortarNoAndMigrationStatus(cortarNo, MigrationStatus.PENDING, pageRequest);
                    
                if (pendingArticles.isEmpty()) {
                    hasMoreData = false;
                    continue;
                }
                
                int batchSize = pendingArticles.getContent().size();
                log.info("지역 {} 배치 처리 중: 페이지 {}, 매물 수: {} (진행률: {}/{})",
                    cortarNo, pageNumber + 1, batchSize, totalProcessed + batchSize, pendingCount);
                    
                for (NaverRawArticle rawArticle : pendingArticles) {
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 마이그레이션 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        rawArticle.markAsFailed(e.getMessage());
                        naverRawArticleRepository.save(rawArticle);
                        totalFailed++;
                    }
                    totalProcessed++;
                }
                
                pageNumber++;
                // 다음 페이지가 있는지 확인
                hasMoreData = pendingArticles.hasNext();
                
                // 메모리 관리를 위해 주기적으로 GC 힌트
                if (pageNumber % 10 == 0) {
                    System.gc();
                }
            }
            
            // 마이그레이션 결과 로깅
            log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 완료 - 총 처리: {}, 성공: {}, 실패: {}",
                cortarNo, totalProcessed, totalSuccess, totalFailed);
                
            // 실패한 항목이 있으면 재시도
            if (totalFailed > 0) {
                log.info("지역 코드 {} 실패한 마이그레이션 재시도 중...", cortarNo);
                retryFailedMigrationsForRegion(cortarNo);
            }
        } catch (Exception e) {
            log.error("지역 코드 {} 마이그레이션 작업 중 오류 발생: {}", cortarNo, e.getMessage(), e);
        }
    }

    /**
     * 특정 지역의 원본 매물 데이터를 마이그레이션합니다.
     * (기존 메서드 - 하위 호환성 유지)
     *
     * @param cortarNo 지역 코드
     */
    public void migrateRawArticlesForRegion(Long cortarNo) {
        migrateAllArticlesForRegion(cortarNo);
    }

    /**
     * 단일 원본 매물 데이터를 마이그레이션합니다.
     *
     * @param rawArticle 원본 매물 데이터
     */
    @Transactional
    public void migrateRawArticle(NaverRawArticle rawArticle) {
        log.info("매물 ID {} 마이그레이션 시작", rawArticle.getArticleId());
        try {
            // JSON 파싱
            JsonNode articleNode = objectMapper.readTree(rawArticle.getRawData());
            
            // 기존 매물 조회
            String articleId = articleNode.path("atclNo").asText();
            Optional<PropertyArticle> existingArticle = propertyArticleRepository.findByArticleId(articleId);
            
            // 매물 생성 또는 업데이트
            PropertyArticle article = PropertyArticle.createOrUpdateFromNaverRawArticle(
                articleNode, 
                String.valueOf(rawArticle.getCortarNo()),
                existingArticle.orElse(null)
            );
            
            // 저장
            propertyArticleRepository.save(article);
            
            // 마이그레이션 성공 상태 업데이트
            rawArticle.updateMigrationStatus(MigrationStatus.COMPLETED);
            naverRawArticleRepository.save(rawArticle);
            
            log.info("매물 ID {} 마이그레이션 완료", rawArticle.getArticleId());
        } catch (Exception e) {
            log.error("매물 ID {} 마이그레이션 중 오류 발생: {}", 
                rawArticle.getArticleId(), e.getMessage(), e);
            throw new RuntimeException("매물 마이그레이션 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 실패한 마이그레이션을 재시도합니다.
     */
    public void retryFailedMigrations() {
        log.info("=== 실패한 마이그레이션 재시도 시작 ===");
        try {
            // 실패한 마이그레이션 수 확인
            long failedCount = naverRawArticleRepository.countByMigrationStatus(MigrationStatus.FAILED);
            if (failedCount == 0) {
                log.info("재시도할 실패한 마이그레이션이 없습니다");
                return;
            }
            
            log.info("재시도 대상 실패한 마이그레이션 수: {}", failedCount);
            int totalProcessed = 0;
            int totalSuccess = 0;
            int totalFailed = 0;
            int pageNumber = 0;
            boolean hasMoreData = true;
            
            while (hasMoreData) {
                PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
                Page<NaverRawArticle> failedArticles = naverRawArticleRepository
                    .findByMigrationStatus(MigrationStatus.FAILED, pageRequest);
                    
                if (failedArticles.isEmpty()) {
                    hasMoreData = false;
                    continue;
                }
                
                int batchSize = failedArticles.getContent().size();
                log.info("실패한 마이그레이션 배치 처리 중: 페이지 {}, 매물 수: {} (진행률: {}/{})",
                    pageNumber + 1, batchSize, totalProcessed + batchSize, failedCount);
                    
                for (NaverRawArticle rawArticle : failedArticles) {
                    // 마이그레이션 상태 초기화
                    rawArticle.resetMigrationStatus();
                    naverRawArticleRepository.save(rawArticle);
                    
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 재시도 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        rawArticle.markAsFailed(e.getMessage());
                        naverRawArticleRepository.save(rawArticle);
                        totalFailed++;
                    }
                    totalProcessed++;
                }
                
                pageNumber++;
                // 다음 페이지가 있는지 확인
                hasMoreData = failedArticles.hasNext();
                
                // 메모리 관리를 위해 주기적으로 GC 힌트
                if (pageNumber % 10 == 0) {
                    System.gc();
                }
            }
            
            log.info("=== 실패한 마이그레이션 재시도 완료 ===");
            log.info("총 처리: {}, 성공: {}, 실패: {}", totalProcessed, totalSuccess, totalFailed);
            
            // 여전히 실패한 항목이 있으면 로그 남기기
            if (totalFailed > 0) {
                log.warn("여전히 {} 개의 마이그레이션이 실패 상태입니다", totalFailed);
            }
        } catch (Exception e) {
            log.error("실패한 마이그레이션 재시도 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 지역의 실패한 마이그레이션을 재시도합니다.
     *
     * @param cortarNo 지역 코드
     */
    public void retryFailedMigrationsForRegion(Long cortarNo) {
        log.info("=== 지역 {} 실패한 마이그레이션 재시도 시작 ===", cortarNo);
        try {
            // 실패한 마이그레이션 수 확인
            long failedCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.FAILED);
                
            if (failedCount == 0) {
                log.info("지역 {} 재시도할 실패한 마이그레이션이 없습니다", cortarNo);
                return;
            }
            
            log.info("지역 {} 재시도 대상 실패한 마이그레이션 수: {}", cortarNo, failedCount);
            int totalProcessed = 0;
            int totalSuccess = 0;
            int totalFailed = 0;
            int pageNumber = 0;
            boolean hasMoreData = true;

                        while (hasMoreData) {
                PageRequest pageRequest = PageRequest.of(pageNumber, BATCH_SIZE);
                Page<NaverRawArticle> failedArticles = naverRawArticleRepository
                    .findByCortarNoAndMigrationStatus(cortarNo, MigrationStatus.FAILED, pageRequest);
                    
                if (failedArticles.isEmpty()) {
                    hasMoreData = false;
                    continue;
                }
                
                int batchSize = failedArticles.getContent().size();
                log.info("지역 {} 실패한 마이그레이션 배치 처리 중: 페이지 {}, 매물 수: {} (진행률: {}/{})",
                    cortarNo, pageNumber + 1, batchSize, totalProcessed + batchSize, failedCount);
                    
                for (NaverRawArticle rawArticle : failedArticles) {
                    // 마이그레이션 상태 초기화 (setter 대신 메서드 사용)
                    naverRawArticleRepository.save(rawArticle.resetMigrationStatus());
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 재시도 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        naverRawArticleRepository.save(rawArticle.markAsFailed(e.getMessage()));
                        totalFailed++;
                    }
                    totalProcessed++;
                }
                
                pageNumber++;
                // 다음 페이지가 있는지 확인
                hasMoreData = failedArticles.hasNext();
            }
            
            log.info("=== 지역 {} 실패한 마이그레이션 재시도 완료 ===", cortarNo);
            log.info("총 처리: {}, 성공: {}, 실패: {}", totalProcessed, totalSuccess, totalFailed);
        } catch (Exception e) {
            log.error("지역 {} 실패한 마이그레이션 재시도 중 오류 발생: {}", cortarNo, e.getMessage(), e);
        }
    }

    /**
     * 특정 지역의 모든 원본 데이터를 마이그레이션 대기 상태로 초기화합니다.
     *
     * @param cortarNo 지역 코드
     * @return 초기화된 데이터 수
     */
    @Transactional
    public int resetMigrationStatusForRegion(Long cortarNo) {
        log.info("지역 {} 마이그레이션 상태 초기화 시작", cortarNo);
        try {
            List<NaverRawArticle> articles = naverRawArticleRepository.findByCortarNo(cortarNo);
            int count = 0;
            
            for (NaverRawArticle article : articles) {
                // setter 대신 메서드 사용
                naverRawArticleRepository.save(article.resetMigrationStatus());
                count++;
            }
            
            log.info("지역 {} 마이그레이션 상태 초기화 완료 - {} 개 데이터", cortarNo, count);
            return count;
        } catch (Exception e) {
            log.error("지역 {} 마이그레이션 상태 초기화 중 오류 발생: {}", cortarNo, e.getMessage(), e);
            throw new RuntimeException("마이그레이션 상태 초기화 실패", e);
        }
    }
    
    /**
     * 특정 지역의 모든 원본 데이터를 마이그레이션 대기 상태로 초기화하고 즉시 마이그레이션합니다.
     *
     * @param cortarNo 지역 코드
     * @return 마이그레이션된 데이터 수
     */
    @Transactional
    public int resetAndMigrateRegion(Long cortarNo) {
        log.info("지역 {} 마이그레이션 상태 초기화 및 즉시 마이그레이션 시작", cortarNo);
        try {
            // 상태 초기화
            int resetCount = resetMigrationStatusForRegion(cortarNo);
            
            if (resetCount == 0) {
                log.info("지역 {} 마이그레이션할 데이터가 없습니다", cortarNo);
                return 0;
            }
            
            // 마이그레이션 수행
            migrateAllArticlesForRegion(cortarNo);
            
            // 성공한 마이그레이션 수 계산
            long successCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.COMPLETED);
                
            log.info("지역 {} 마이그레이션 상태 초기화 및 즉시 마이그레이션 완료 - 총 {}/{} 성공", 
                cortarNo, successCount, resetCount);
                
            return (int) successCount;
        } catch (Exception e) {
            log.error("지역 {} 마이그레이션 상태 초기화 및 즉시 마이그레이션 중 오류 발생: {}", 
                cortarNo, e.getMessage(), e);
            throw new RuntimeException("마이그레이션 상태 초기화 및 즉시 마이그레이션 실패", e);
        }
    }
    
    /**
     * 마이그레이션 통계 정보를 반환합니다.
     *
     * @return 마이그레이션 통계 정보
     */
    public MigrationStatisticsDTO getMigrationStatistics() {
        try {
            long pendingCount = naverRawArticleRepository.countByMigrationStatus(MigrationStatus.PENDING);
            long completedCount = naverRawArticleRepository.countByMigrationStatus(MigrationStatus.COMPLETED);
            long failedCount = naverRawArticleRepository.countByMigrationStatus(MigrationStatus.FAILED);
            long totalCount = pendingCount + completedCount + failedCount;
            
            return MigrationStatisticsDTO.builder()
                .totalArticles(totalCount)
                .pendingArticles(pendingCount)
                .completedArticles(completedCount)
                .failedArticles(failedCount)
                .completionRate(totalCount > 0 ? (double) completedCount / totalCount * 100 : 0)
                .failureRate(totalCount > 0 ? (double) failedCount / totalCount * 100 : 0)
                .timestamp(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("마이그레이션 통계 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("마이그레이션 통계 정보 조회 실패", e);
        }
    }
    
    /**
     * 특정 지역의 마이그레이션 통계 정보를 반환합니다.
     *
     * @param cortarNo 지역 코드
     * @return 마이그레이션 통계 정보
     */
    public MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo) {
        try {
            long pendingCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.PENDING);
            long completedCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.COMPLETED);
            long failedCount = naverRawArticleRepository.countByCortarNoAndMigrationStatus(
                cortarNo, MigrationStatus.FAILED);
            long totalCount = pendingCount + completedCount + failedCount;
            
            return MigrationStatisticsDTO.builder()
                .regionCode(cortarNo)
                .totalArticles(totalCount)
                .pendingArticles(pendingCount)
                .completedArticles(completedCount)
                .failedArticles(failedCount)
                .completionRate(totalCount > 0 ? (double) completedCount / totalCount * 100 : 0)
                .failureRate(totalCount > 0 ? (double) failedCount / totalCount * 100 : 0)
                .timestamp(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("지역 {} 마이그레이션 통계 정보 조회 중 오류 발생: {}", cortarNo, e.getMessage(), e);
            throw new RuntimeException("마이그레이션 통계 정보 조회 실패", e);
        }
    }
}

