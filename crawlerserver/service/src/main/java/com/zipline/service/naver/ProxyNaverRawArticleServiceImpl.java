package com.zipline.service.naver;

import com.zipline.global.task.TaskDefinition;
import com.zipline.global.task.TaskExecutionHandler;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskType;
import com.zipline.service.naver.crawler.ProxyNaverRegionCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyNaverRawArticleServiceImpl implements ProxyNaverRawArticleService {

	private final TaskExecutionHandler taskExecutionHandler;
	private final ProxyNaverRegionCrawler proxyRegionCrawler;

	@Override
	public TaskResponseDto crawlAndSaveRawArticles() {
		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 프록시 전체 지역 매물 수집",
						proxyRegionCrawler::executeCrawl,
						null
				)
		);
	}

	@Override
	public TaskResponseDto crawlAndSaveRawArticlesForRegion(Long cortarNo) {
		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 프록시 특정 지역 매물 수집",
						proxyRegionCrawler::executeCrawlForRegion,
						cortarNo
				)
		);
	}
}