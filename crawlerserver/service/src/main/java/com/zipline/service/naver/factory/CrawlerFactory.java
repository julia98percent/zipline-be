package com.zipline.service.naver.factory;

import com.zipline.service.naver.crawler.NaverArticleCrawler;
import com.zipline.service.naver.crawler.ParallelNaverArticleCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrawlerFactory {

    private final NaverArticleCrawler singleThreadCrawler;
    private final ParallelNaverArticleCrawler parallelCrawler;

    public NaverArticleCrawler getCrawler(boolean useProxy) {
        return useProxy ? parallelCrawler : singleThreadCrawler;
    }
}
