package com.zipline.service.zigbang;

import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.service.zigbang.crawler.ZigBangArticleCrawler;
import com.zipline.service.zigbang.factory.ZigBangCrawlerFactory;
import com.zipline.service.task.TaskDefinition;
import com.zipline.service.task.TaskExecutionHandler;
import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZigBangArticleServiceImpl implements com.zipline.service.zigbang.ZigBangArticleService {

	private final ZigBangCrawlerFactory ZigBangCrawlerFactory;
	private final TaskExecutionHandler taskExecutionHandler;

	@Autowired
	@Qualifier("defaultFetcher")
	private Fetcher defaultFetcher;

	@Autowired
	@Qualifier("proxyFetcher")
	private Fetcher proxyFetcher;

	@Override
	public TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy) {
		ZigBangArticleCrawler crawler = ZigBangCrawlerFactory.getCrawler(useProxy);
		Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

		return taskExecutionHandler.execute(
				TaskDefinition.of(
						TaskType.ZIGBANGCRAWLING,
						"직방 전체 지역 매물 수집",
						() -> crawler.executeCrawl(fetcher)
				)
		);
	}
};
