package com.zipline.service.zigbang.crawler;

import com.zipline.infrastructure.crawl.fetch.Fetcher;

public interface ZigBangArticleCrawler {
    void executeCrawl(Fetcher fetcher);
}