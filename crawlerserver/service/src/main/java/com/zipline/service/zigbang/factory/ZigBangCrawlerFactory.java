package com.zipline.service.zigbang.factory;

import com.zipline.service.zigbang.crawler.ZigBangArticleCrawler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ZigBangCrawlerFactory {

    private final ZigBangArticleCrawler defaultCrawler;
    private final ZigBangArticleCrawler parallelCrawler;

    public ZigBangCrawlerFactory(
            @Qualifier("defaultZigBangArticleCrawler") ZigBangArticleCrawler defaultCrawler,
            @Qualifier("parallelZigBangArticleCrawler") ZigBangArticleCrawler parallelCrawler) {
        this.defaultCrawler = defaultCrawler;
        this.parallelCrawler = parallelCrawler;
    }

    public ZigBangArticleCrawler getCrawler(boolean useProxy) {
        return useProxy ? parallelCrawler : defaultCrawler;
    }
}
