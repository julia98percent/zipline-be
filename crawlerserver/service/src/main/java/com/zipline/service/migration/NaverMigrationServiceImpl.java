package com.zipline.service.migration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.naver.NaverRawArticle;
import com.zipline.domain.entity.publicitem.PropertyArticle;
import com.zipline.global.exception.migration.MigrationException;
import com.zipline.global.exception.migration.errorcode.MigrationErrorCode;
import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.Task;
import com.zipline.global.task.TaskManager;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import com.zipline.infrastructure.naver.NaverRawArticleRepository;
import com.zipline.infrastructure.publicItem.PropertyArticleRepository;
import com.zipline.infrastructure.region.RegionRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverMigrationServiceImpl implements NaverMigrationService {
	private final TaskManager taskManager;
	private final TaskExecutor taskExecutor;
	private final RegionRepository regionRepository;
	private final NaverRawArticleRepository naverRawArticleRepository;
	private final PropertyArticleRepository propertyArticleRepository;
	private final ObjectMapper objectMapper;

	private static final int BATCH_SIZE = 100;

	@Override
	public TaskResponseDto startFullMigration() {
		if (taskManager.isTaskRunning(TaskType.MIGRATION)){
			throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);}
		Task task = taskManager.createTask(TaskType.MIGRATION);
		try {
			CompletableFuture.runAsync(() -> {
				executeFullMigrationAsync(task);
			}, taskExecutor);
		} catch (Exception e) {
			log.error("마이그레이션 작업 실행중 오류 발생: {}", e.getMessage(), e);
			taskManager.removeTask(TaskType.MIGRATION);
		}
		taskManager.removeTask(TaskType.MIGRATION);
		return TaskResponseDto.fromTask(task);
	}

	private void executeFullMigrationAsync(Task task) {
		task.markAsRunning();
		taskManager.updateTaskStatus(TaskType.MIGRATION, TaskStatus.RUNNING);
		log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 시작 ===");
		List<Long> allRegionCodes = regionRepository.findAllCortarNos();
		log.info("총 {} 개 지역에 대한 마이그레이션 시작", allRegionCodes.size());
		task.markAsRunning();
		for (Long cortarNo : allRegionCodes) {
			try {
				executeRegionMigrationAsync(task, cortarNo);
				log.info("지역 {} 마이그레이션 완료", cortarNo);
				task.markAsCompleted();
			} catch (Exception e) {
				log.error("지역 {} 마이그레이션 중 오류 발생: {}", cortarNo, e.getMessage());
				task.markAsFailed(e.getMessage());
			}
		}
		log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 완료 ===");
	}

	@Override
	public TaskResponseDto migrateRegion(Long regionId) {
		if (taskManager.isTaskRunning(TaskType.MIGRATION)){
			log.error("마이그레이션 작업이 이미 실행 중입니다.");
			throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
		}
		Task task = taskManager.createRegionalTask(TaskType.MIGRATION, regionId);
		try {
			CompletableFuture.runAsync(() -> {
				executeRegionMigrationAsync(task, regionId);
			}, taskExecutor);
		} catch (Exception e) {
			log.error("지역 {} 마이그레이션 작업 중 오류 발생: {}", regionId, e.getMessage(), e);
			task.markAsFailed(e.getMessage());
			// 추후 실패작업 재실행 구현시 삭제
			taskManager.removeTask(TaskType.MIGRATION);
		}
		taskManager.removeTask(TaskType.MIGRATION);
		return TaskResponseDto.fromTask(task);
	}

	private void executeRegionMigrationAsync(Task task, Long regionId) {
		log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 시작", regionId);
		boolean hasMoreData = true;
		int pageNumber = 0;
		int failedCount = 0;

		while (hasMoreData) {
			PageRequest pageRequest = PageRequest.of(0, BATCH_SIZE);
			Page<NaverRawArticle> pendingArticle = naverRawArticleRepository.findByCortarNoAndMigrationStatus(regionId, MigrationStatus.PENDING, pageRequest);
			int batchSize = pendingArticle.getSize();

			for (NaverRawArticle rawArticle : pendingArticle) {
				try {
					migrateRawArticle(rawArticle);
				} catch (Exception e) {
					log.error("지역 코드 {} 마이그레이션 중 오류 발생: {}", regionId, e.getMessage(), e);
					task.markAsFailed(e.getMessage());
					failedCount++;
					throw new MigrationException(MigrationErrorCode.MIGRATION_FAILED);
				}
			}
			pageNumber++;
			hasMoreData = pendingArticle.hasNext();
			// 메모리 관리를 위해 주기적으로 GC 힌트
			if (pageNumber % 10 == 0) {
				System.gc();
			}
		}
		if (failedCount > 0) {
			log.error("{}개 지역 마이그레이션 중 오류 발생",  failedCount);
		}
		log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 완료", regionId);
	}

	@Transactional
	private void migrateRawArticle(NaverRawArticle rawArticle){
		log.info("네이버 원본 매물 데이터 마이그레이션 시작", rawArticle.getArticleId());
		try{
			JsonNode articleNode = objectMapper.readTree(rawArticle.getRawData());

			String ArticleId = articleNode.get("atclNo").asText();
			Optional<PropertyArticle> existingArticle = propertyArticleRepository.findByArticleId(ArticleId);

			PropertyArticle article = PropertyArticle.createOrUpdateFromNaverRawArticle(
					articleNode,
					String.valueOf(rawArticle.getCortarNo()),
					existingArticle.orElse(null)
			);
			propertyArticleRepository.save(article);
			rawArticle.updateMigrationStatus(MigrationStatus.COMPLETED);
			naverRawArticleRepository.save(rawArticle);
			log.info("네이버 원본 매물 데이터 마이그레이션 완료", rawArticle.getArticleId());
		} catch (Exception e) {
			log.error("네이버 원본 매물 데이터 마이그레이션 중 오류 발생: {}", e.getMessage(), e);
			throw new MigrationException(MigrationErrorCode.MIGRATION_FAILED);
		}
	}

	@Override
	public TaskResponseDto getTaskStatus(TaskType taskName) {
		Task task = taskManager.getTaskByType(taskName);
		return TaskResponseDto.fromTask(task);
	}

//	@Override
//	public MigrationStatisticsDTO getMigrationStatistics() {
//		MigrationStatisticsDTO statistics = migrationRepository.getMigrationStatistics();
//		return statics;
//	}
//
//	@Override
//	public MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo) {
//		MigrationStatisticsDTO statistics = migrationRepository.getMigrationStatisticsForRegion(cortarNo);
//		return statics;
//	}
}