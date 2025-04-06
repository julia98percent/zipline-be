package com.zipline.service.publicItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.entity.publicItem.NaverRawArticle;
import com.zipline.entity.publicItem.PropertyArticle;
import com.zipline.entity.publicItem.Region;
import com.zipline.entity.publicItem.enums.Category;
import com.zipline.entity.publicItem.enums.MigrationStatus;
import com.zipline.entity.publicItem.enums.Platform;
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

    /**
     * 마이그레이션이 필요한 모든 원본 매물 데이터를 처리합니다.
     * 스케줄링된 작업으로 실행됩니다.
     */
    @Scheduled(fixedDelay = 300000) // 5분마다 실행
    public void scheduledMigration() {
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
            int pageNumber = 0;
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
                    cortarNo, pageNumber + 1, batchSize, 
                    totalProcessed + batchSize, pendingCount);
                
                for (NaverRawArticle rawArticle : pendingArticles) {
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 마이그레이션 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        markAsFailed(rawArticle, e.getMessage());
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
            
            // 기존 매물 조회 또는 새로 생성
            String articleId = articleNode.path("atclNo").asText();
            Optional<PropertyArticle> existingArticle = propertyArticleRepository.findByArticleId(articleId);
            
            PropertyArticle article;
            if (existingArticle.isPresent()) {
                article = existingArticle.get();
                log.info("기존 매물 정보 업데이트: {}", articleId);
            } else {
                article = new PropertyArticle();
                article.setArticleId(articleId);
                article.setRegionCode(String.valueOf(rawArticle.getCortarNo()));
                article.setPlatform(Platform.NAVER);
                article.setPlatformUrl("https://new.land.naver.com/articles/" + articleId);
                article.setCreatedAt(LocalDateTime.now());
                log.info("새로운 매물 정보 생성: {}", articleId);
            }
            
            // 기본 정보 업데이트
            article.setBuildingName(articleNode.path("atclNm").asText());
            article.setDescription(articleNode.path("atclFetrDesc").asText());
            article.setBuildingType(articleNode.path("rletTpNm").asText());
            
            // 거래 유형 및 가격 정보 설정
            String tradTpNm = articleNode.path("tradTpNm").asText();
            switch (tradTpNm) {
                case "매매":
                    article.setCategory(Category.SALE);
                    article.setPrice(articleNode.path("prc").asLong());
                    break;
                case "전세":
                    article.setCategory(Category.DEPOSIT);
                    article.setDeposit(articleNode.path("prc").asLong());
                    break;
                case "월세":
                    article.setCategory(Category.MONTHLY);
                    article.setDeposit(articleNode.path("prc").asLong());
                    article.setMonthlyRent(articleNode.path("rentPrc").asLong());
                    break;
            }
            
            // 위치 및 면적 정보 업데이트
            article.setLongitude(articleNode.path("lng").asDouble());
            article.setLatitude(articleNode.path("lat").asDouble());
            article.setSupplyArea(articleNode.path("spc1").asDouble());
            article.setExclusiveArea(articleNode.path("spc2").asDouble());
            article.setUpdatedAt(LocalDateTime.now());
            
            // 저장
            propertyArticleRepository.save(article);
            
            // 마이그레이션 성공 상태 업데이트
            rawArticle.setMigrationStatus(MigrationStatus.COMPLETED);
            rawArticle.setMigratedAt(LocalDateTime.now());
            naverRawArticleRepository.save(rawArticle);
            
            log.info("매물 ID {} 마이그레이션 완료", rawArticle.getArticleId());
        } catch (Exception e) {
            log.error("매물 ID {} 마이그레이션 중 오류 발생: {}", rawArticle.getArticleId(), e.getMessage(), e);
            throw new RuntimeException("매물 마이그레이션 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 마이그레이션 실패 상태로 표시합니다.
     * 
     * @param rawArticle 원본 매물 데이터
     * @param errorMessage 오류 메시지
     */
    @Transactional
    private void markAsFailed(NaverRawArticle rawArticle, String errorMessage) {
        rawArticle.setMigrationStatus(MigrationStatus.FAILED);
        rawArticle.setMigrationError(errorMessage);
        rawArticle.setMigratedAt(LocalDateTime.now());
        naverRawArticleRepository.save(rawArticle);
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
                    rawArticle.setMigrationStatus(MigrationStatus.PENDING);
                    rawArticle.setMigrationError(null);
                    naverRawArticleRepository.save(rawArticle);
                    
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 재시도 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        markAsFailed(rawArticle, e.getMessage());
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
                    // 마이그레이션 상태 초기화
                    rawArticle.setMigrationStatus(MigrationStatus.PENDING);
                    rawArticle.setMigrationError(null);
                    naverRawArticleRepository.save(rawArticle);
                    
                    try {
                        migrateRawArticle(rawArticle);
                        totalSuccess++;
                    } catch (Exception e) {
                        log.error("매물 ID {} 재시도 중 오류 발생: {}", 
                            rawArticle.getArticleId(), e.getMessage());
                        markAsFailed(rawArticle, e.getMessage());
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
                article.setMigrationStatus(MigrationStatus.PENDING);
                article.setMigrationError(null);
                article.setMigratedAt(null);
                naverRawArticleRepository.save(article);
                count++;
            }
            
            log.info("지역 {} 마이그레이션 상태 초기화 완료 - {} 개 데이터", cortarNo, count);
            return count;
        } catch (Exception e) {
            log.error("지역 {} 마이그레이션 상태 초기화 중 오류 발생: {}", cortarNo, e.getMessage(), e);
            throw new RuntimeException("마이그레이션 상태 초기화 실패", e);
        }
    }
}
