package com.zipline.service.naver;

import com.zipline.global.task.TaskDefinition;
import com.zipline.global.task.TaskExecutionHandler;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskType;
import com.zipline.service.naver.crawler.NaverRegionCrawler;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverRawArticleServiceImpl implements NaverRawArticleService {

	private final TaskExecutionHandler taskExecutionHandler;
	private final NaverRegionCrawler regionCrawler;

	/**
	 * 전체 지역 크롤링 시작
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticles() {
		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 전체 지역 매물 수집",
						regionCrawler::executeCrawl,
						null
				)
		);
	}

	/**
	 * 특정 지역 크롤링 시작
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticlesForRegion(Long cortarNo) {
		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 특정 지역 매물 수집",
						regionCrawler::executeCrawlForRegion,
						cortarNo
				)
		);
	}
}
