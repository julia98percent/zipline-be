package com.zipline.global.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class CrawlingStatusManager {
    private final AtomicBoolean isCrawling = new AtomicBoolean(false);

    public boolean isCrawling() {
        return isCrawling.get();
    }

    public void startCrawling() {
        if (!isCrawling.compareAndSet(false, true)) {
            throw new IllegalStateException("다른 크롤링 작업이 진행 중입니다.");
        }
    }

    public void endCrawling() {
        isCrawling.set(false);
    }

    public <T> T executeWithLock(Supplier<T> task) {
        startCrawling();
        try {
            return task.get();
        } finally {
            endCrawling();
        }
    }
}
