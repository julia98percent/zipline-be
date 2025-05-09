package com.zipline.service.naver.factory;

import com.zipline.service.naver.crawler.NaverArticleCrawler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NaverCrawlerFactory {

    private final NaverArticleCrawler defaultCrawler;
    private final NaverArticleCrawler parallelCrawler;

    public NaverCrawlerFactory(
            @Qualifier("naverArticleCrawler") NaverArticleCrawler defaultCrawler,
            @Qualifier("parallelNaverArticleCrawler") NaverArticleCrawler parallelCrawler) {
        this.defaultCrawler = defaultCrawler;
        this.parallelCrawler = parallelCrawler;
    }

    public NaverArticleCrawler getCrawler(boolean useProxy) {
        return useProxy ? parallelCrawler : defaultCrawler;
    }
}