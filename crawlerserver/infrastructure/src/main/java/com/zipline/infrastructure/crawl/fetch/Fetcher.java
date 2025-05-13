package com.zipline.infrastructure.crawl.fetch;

import com.zipline.infrastructure.crawl.fetch.dto.FetchConfigDTO;

public interface Fetcher {
    String fetch(String url, FetchConfigDTO config) throws Exception;
    String fetchPost(String url, String body, FetchConfigDTO config) throws Exception;
}
