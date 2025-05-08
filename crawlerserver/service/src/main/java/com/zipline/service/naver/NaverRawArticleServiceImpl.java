package com.zipline.service.naver;

import com.zipline.service.naver.crawler.NaverArticleCrawler;
import com.zipline.service.naver.factory.CrawlerFactory;
import com.zipline.service.task.TaskDefinition;
import com.zipline.service.task.TaskExecutionHandler;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;
import com.zipline.infrastructure.crawl.fetch.Fetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NaverRawArticleServiceImpl implements NaverRawArticleService {

	private final CrawlerFactory crawlerFactory;
	private final TaskExecutionHandler taskExecutionHandler;

	@Autowired
	@Qualifier("defaultFetcher")
	private Fetcher defaultFetcher;

	@Autowired
	@Qualifier("proxyFetcher")
	private Fetcher proxyFetcher;

	@Override
	public TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy) {

		//NOTE: 요청방식과 크롤링방식에 대한 유연성을 위해 별도로 구현했습니다.
		NaverArticleCrawler crawler = crawlerFactory.getCrawler(useProxy);
		Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 전체 지역 매물 수집",
						() -> crawler.executeCrawl(fetcher)
				)
		);
	}

	@Override
	public TaskResponseDto crawlAndSaveRawArticlesForRegion(Boolean useProxy, Long cortarNo) {
		NaverArticleCrawler crawler = crawlerFactory.getCrawler(useProxy);
		Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.NAVERCRAWLING,
						"네이버 특정 지역 매물 수집",
						() -> crawler.executeCrawlForRegion(fetcher, cortarNo)
				)
		);
	}
};
