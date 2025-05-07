package com.zipline.service.naver;

import com.zipline.service.naver.crawler.NaverArticleCrawler;
import com.zipline.service.task.TaskDefinition;
import com.zipline.service.task.TaskExecutionHandler;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverRawArticleServiceImpl implements NaverRawArticleService {
	private final NaverArticleCrawler crawler;
	private final TaskExecutionHandler taskExecutionHandler;

	@Autowired
	@Qualifier("defaultFetcher")
	private Fetcher defaultFetcher;

	@Autowired
	@Qualifier("proxyFetcher")
	private Fetcher proxyFetcher;

	/**
	 * 전체 지역 크롤링 시작 (프록시 사용 여부에 따라 다름)
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy) {
		Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 전체 지역 매물 수집",
						() -> crawler.executeCrawl(fetcher)
				)
		);
	}

	/**
	 * 특정 지역 크롤링 시작
	 */
	@Override
	public TaskResponseDto crawlAndSaveRawArticlesForRegion(Boolean useProxy, Long cortarNo) {
		Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 특정 지역 매물 수집",
						() -> crawler.executeCrawlForRegion(fetcher, cortarNo)
				)
		);
	}
}
