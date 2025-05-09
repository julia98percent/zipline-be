package com.zipline.service.region.factory;

import com.zipline.service.region.crawler.RegionCrawler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RegionCrawlerFactory {

    private final RegionCrawler naverRegionCrawler;

    public RegionCrawlerFactory(
            @Qualifier("RegionCrawler") RegionCrawler naverRegionCrawler) {
        this.naverRegionCrawler = naverRegionCrawler;
    }

    public RegionCrawler getCrawler(boolean useProxy) {
        return naverRegionCrawler;
    }
}