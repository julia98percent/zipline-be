package com.zipline.service.migration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.zipline.global.task.Task;
import com.zipline.global.task.TaskManager;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.infrastructure.region.RegionRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.service.migration.dto.MigrationStatisticsDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverMigrationServiceImpl implements NaverMigrationService {
	private final NaverMigrationService migrationService;
	private final TaskManager taskManager;
	private final TaskExecutor taskExecutor;
	private final RegionRepository regionRepository;

	private static final String FULL_MIGRATION_TASK = "NAVER_FULL_MIGRATION";
	private static final String REGION_MIGRATION_TASK = "NAVER_REGION_MIGRATION";
	private static final String RETRY_FAILED_TASK = "NAVER_RETRY_FAILED";

	@Override
	public TaskResponseDto startFullMigration() {
		// 작업 생성
		Task task = taskManager.createTask(FULL_MIGRATION_TASK);

		// 비동기 작업 실행
		CompletableFuture.runAsync(() -> {
			try {
				task.markAsRunning();
				taskManager.updateTaskStatus(task);

				log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 시작 ===");

				// 1. 모든 지역 코드 조회
				List<Long> allRegionCodes = regionRepository.findAllCortarNos();
				log.info("총 {} 개 지역에 대한 마이그레이션 시작", allRegionCodes.size());

				task.updateProgress(allRegionCodes.size(), 0, 0, 0);
				taskManager.updateTaskStatus(task);

				int totalRegions = allRegionCodes.size();
				int processedRegions = 0;
				int successRegions = 0;
				int failedRegions = 0;

				// 2. 각 지역별로 마이그레이션 수행
				for (Long cortarNo : allRegionCodes) {
					try {
						log.info("지역 {} 마이그레이션 시작 ({}/{})",
								cortarNo, ++processedRegions, totalRegions);

						// 지역별 마이그레이션 수행
						migrationService.migrateRegion(cortarNo);
						log.info("지역 {} 마이그레이션 완료", cortarNo);
						successRegions++;
					} catch (Exception e) {
						log.error("지역 {} 마이그레이션 중 오류 발생: {}", cortarNo, e.getMessage());
						failedRegions++;
					}

					// 진행 상황 업데이트
					task.updateProgress(totalRegions, processedRegions, successRegions, failedRegions);
					taskManager.updateTaskStatus(task);
				}

				// 3. 실패한 마이그레이션 재시도
				migrationService.retryFailedMigrations();

				task.markAsCompleted();
				taskManager.updateTaskStatus(task);
				log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 완료 ===");
			} catch (Exception e) {
				log.error("마이그레이션 작업 중 오류 발생: {}", e.getMessage(), e);
				task.markAsFailed(e.getMessage());
				taskManager.updateTaskStatus(task);
			}
		}, taskExecutor);

		return TaskResponseDto.fromTask(task);
	}

	@Override
	public TaskResponseDto migrateRegion(Long regionId) {
		String taskType = REGION_MIGRATION_TASK + "_" + regionId;

		// 작업 생성
		Task task = taskManager.createRegionalTask(taskType, regionId);

		// 비동기 작업 실행
		CompletableFuture.runAsync(() -> {
			try {
				task.markAsRunning();
				taskManager.updateTaskStatus(task);

				log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 시작", regionId);

				// 마이그레이션 수행
				migrationService.migrateRegion(regionId);

				// 마이그레이션 통계 조회
				MigrationStatisticsDTO stats = migrationService.getMigrationStatisticsForRegion(regionId);

				// 진행 상황 업데이트
				task.updateProgress(
						stats.getTotalArticles(),
						stats.getCompletedArticles() + stats.getFailedArticles(),
						stats.getCompletedArticles(),
						stats.getFailedArticles()
				);

				task.markAsCompleted();
				taskManager.updateTaskStatus(task);
				log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 완료", regionId);
			} catch (Exception e) {
				log.error("지역 {} 마이그레이션 작업 중 오류 발생: {}", regionId, e.getMessage(), e);
				task.markAsFailed(e.getMessage());
				taskManager.updateTaskStatus(task);
			}
		}, taskExecutor);

		return TaskResponseDto.fromTask(task);
	}

	@Override
	public TaskResponseDto retryFailedMigrations() {
		// 작업 생성
		Task task = taskManager.createTask(RETRY_FAILED_TASK);

		// 비동기 작업 실행
		CompletableFuture.runAsync(() -> {
			try {
				task.markAsRunning();
				taskManager.updateTaskStatus(task);

				log.info("=== 실패한 마이그레이션 재시도 시작 ===");

				// 실패한 마이그레이션 수 확인
				long failedCount = migrationService.getMigrationStatistics().getFailedArticles();

				// 작업 시작
				task.updateProgress(failedCount, 0, 0, 0);
				taskManager.updateTaskStatus(task);

				// 실패한 마이그레이션 재시도
				migrationService.retryFailedMigrations();

				// 마이그레이션 통계 조회
				MigrationStatisticsDTO stats = migrationService.getMigrationStatistics();

				// 진행 상황 업데이트
				task.updateProgress(
						failedCount,
						failedCount,
						failedCount - stats.getFailedArticles(),
						stats.getFailedArticles()
				);

				task.markAsCompleted();
				taskManager.updateTaskStatus(task);
				log.info("=== 실패한 마이그레이션 재시도 완료 ===");
			} catch (Exception e) {
				log.error("실패한 마이그레이션 재시도 중 오류 발생: {}", e.getMessage(), e);
				task.markAsFailed(e.getMessage());
				taskManager.updateTaskStatus(task);
			}
		}, taskExecutor);

		return TaskResponseDto.fromTask(task);
	}

	@Override
	public TaskResponseDto getTaskStatus(String taskId) {
		Task task = taskManager.getTaskById(taskId);
		return TaskResponseDto.fromTask(task);
	}

	@Override
	public MigrationStatisticsDTO getMigrationStatistics() {
		return migrationService.getMigrationStatistics();
	}

	@Override
	public MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo) {
		return migrationService.getMigrationStatisticsForRegion(cortarNo);
	}
}
