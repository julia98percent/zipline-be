package com.zipline.service.migration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.enums.Platform;
import com.zipline.domain.entity.migration.Migration;
import com.zipline.domain.entity.naver.NaverRawArticle;
import com.zipline.domain.entity.publicitem.PropertyArticle;
import com.zipline.global.exception.migration.MigrationException;
import com.zipline.global.exception.migration.errorcode.MigrationErrorCode;
import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.infrastructure.migration.MigrationRepository;
import com.zipline.service.task.Task;
import com.zipline.service.task.TaskManager;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;
import com.zipline.infrastructure.naver.NaverRawArticleRepository;
import com.zipline.infrastructure.publicItem.PropertyArticleRepository;
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

	private final MigrationRepository migrationRepository;
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
		log.info("=== 네이버 원본 매물 데이터 마이그레이션 작업 시작 ===");
		List<Long> allRegionCodes = migrationRepository.findAllCortarNos();
		log.info("총 {} 개 지역에 대한 마이그레이션 시작", allRegionCodes.size());
		for (Long cortarNo : allRegionCodes) {
			try {
				executeRegionMigrationAsync(task, cortarNo);
				log.info("지역 {} 마이그레이션 완료", cortarNo);
			} catch (Exception e) {
				String errorMessage = String.format("지역 %s 마이그레이션 중 오류 발생: %s", cortarNo, e.getMessage());
				log.error("지역 {} 마이그레이션 중 오류 발생: {}", cortarNo, e.getMessage());
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
		Task task = taskManager.createTask(TaskType.MIGRATION, regionId);
		try {
			CompletableFuture.runAsync(() -> {
				executeRegionMigrationAsync(task, regionId);
			}, taskExecutor);
		} catch (Exception e) {
			log.error("지역 {} 마이그레이션 작업 중 오류 발생: {}", regionId, e.getMessage(), e);
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
					failedCount++;
					String errorMessage = String.format("지역 코드 %s 마이그레이션 중 오류 발생: 실패 카운트{} message: {}", regionId, failedCount, e.getMessage(), e);
					Migration migration = migrationRepository.findByCortarNo(regionId);
					migration.errorWithLog(Platform.NAVER, errorMessage, 1000, MigrationStatus.FAILED);
					migrationRepository.save(migration);
					log.error(errorMessage, e);
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
			throw new MigrationException(MigrationErrorCode.MIGRATION_FAILED);
		}
		log.info("지역 코드 {} 원본 매물 데이터 마이그레이션 완료", regionId);
	}
	@Transactional
	private void migrateRawArticle(NaverRawArticle rawArticle) {
		log.info("네이버 원본 매물 데이터 {} 마이그레이션 시작", rawArticle.getArticleId());

		try {
			JsonNode articleNode = objectMapper.readTree(rawArticle.getRawData());
			String articleId = articleNode.path("atclNo").asText();

			// 기존 데이터가 있다면 삭제 (ID 재사용 대비)
			propertyArticleRepository.deleteByArticleId(articleId);

			// 새롭게 생성
			PropertyArticle newArticle = PropertyArticle.createFromNaverRawArticle(articleNode, String.valueOf(rawArticle.getCortarNo()));
			propertyArticleRepository.save(newArticle);

			// 상태 업데이트
			rawArticle.updateMigrationStatus(MigrationStatus.COMPLETED);
			naverRawArticleRepository.save(rawArticle);

			log.info("네이버 원본 매물 데이터 {} 마이그레이션 완료", rawArticle.getArticleId());

		} catch (Exception e) {
			log.error("네이버 원본 매물 데이터 {} 마이그레이션 중 오류 발생", rawArticle.getArticleId(), e);
			throw new MigrationException(MigrationErrorCode.MIGRATION_FAILED);
		}
	}

	@Override
	public TaskResponseDto getTaskStatus(TaskType taskName) {
		Task task = taskManager.getTaskByType(taskName);
		return TaskResponseDto.fromTask(task);
	}
}