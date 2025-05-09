package com.zipline.service.region;

import com.zipline.infrastructure.crawl.fetch.Fetcher;
import com.zipline.service.region.crawler.RegionCrawler;
import com.zipline.service.region.factory.RegionCrawlerFactory;
import com.zipline.service.task.TaskDefinition;
import com.zipline.service.task.TaskExecutionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionCodeServiceImpl implements RegionCodeService {

    private final RegionCrawlerFactory crawlerFactory;
    private final TaskExecutionHandler taskExecutionHandler;

    @Autowired
    @Qualifier("defaultFetcher")
    private Fetcher defaultFetcher;

    @Autowired
    @Qualifier("proxyFetcher")
    private Fetcher proxyFetcher;

    @Override
    public TaskResponseDto crawlAndSaveRegions(Boolean useProxy) {
        RegionCrawler crawler = crawlerFactory.getCrawler(useProxy);
        Fetcher fetcher = useProxy ? proxyFetcher : defaultFetcher;

        return taskExecutionHandler.execute(
                TaskDefinition.of(
                        TaskType.NAVERCRAWLING,
                        "네이버 전체 지역 수집",
                        () -> crawler.executeCrawl(fetcher)
                )
        );
    }

    @Override
    public TaskResponseDto crawlAndSaveRegionsForRegion(Boolean useProxy, Long cortarNo) {
        RegionCrawler crawler = crawlerFactory.getCrawler(useProxy);
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
