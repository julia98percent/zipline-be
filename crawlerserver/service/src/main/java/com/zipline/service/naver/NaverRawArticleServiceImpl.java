package com.zipline.service.naver;

import java.util.concurrent.CompletableFuture;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.Task;
import com.zipline.global.task.TaskManager;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskType;
import com.zipline.service.naver.client.NaverApiClient;
import com.zipline.service.naver.crawler.NaverRegionCrawler;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverRawArticleServiceImpl implements NaverRawArticleService {


	private final NaverRegionCrawler regionCrawler;
	private final TaskManager taskManager;
	private final TaskExecutor taskExecutor;

	/**
	 * 전체 지역의 매물 수집 시작 (비동기)
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticles() {
		if (taskManager.isTaskRunning(TaskType.NAVERCRAWLING)) {
			throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
		}
		Task task = taskManager.createTask(TaskType.NAVERCRAWLING);
		try {CompletableFuture.runAsync(() -> regionCrawler.executeCrawl(task), taskExecutor);
		} catch (Exception e) {
			log.error("전체 지역 크롤링 실행 실패", e);
			taskManager.removeTask(TaskType.NAVERCRAWLING);
			throw new RuntimeException("크롤러 실행 실패", e);}
		taskManager.removeTask(TaskType.NAVERCRAWLING);
		return TaskResponseDto.fromTask(task);
	}

	/**
	 * 특정 지역의 매물 수집 시작 (비동기)
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticlesForRegion(Long cortarNo) {
		if (taskManager.isTaskRunning(TaskType.NAVERCRAWLING)) {
			throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
		}
		Task task = taskManager.createTask(TaskType.NAVERCRAWLING);
		try {CompletableFuture.runAsync(() -> regionCrawler.executeCrawlForRegion(task, cortarNo), taskExecutor);
		} catch (Exception e) {
			log.error("지역 {} 크롤링 실행 실패", cortarNo, e);
			taskManager.removeTask(TaskType.NAVERCRAWLING);
			throw new RuntimeException("지역 크롤러 실행 실패", e);}
		taskManager.removeTask(TaskType.NAVERCRAWLING);
		return TaskResponseDto.fromTask(task);
	}
}
