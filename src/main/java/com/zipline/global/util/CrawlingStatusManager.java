package com.zipline.global.util;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrawlingStatusManager {
  private volatile boolean isCrawling = false;

  public synchronized boolean isCrawling() {
    return isCrawling;
  }

  public synchronized void startCrawling() {
    if (isCrawling) {
      throw new IllegalStateException("다른 크롤링 작업이 진행 중입니다.");
    }
    isCrawling = true;
    log.info("=== 크롤링 작업 시작 ===");
  }

  public synchronized void endCrawling() {
    isCrawling = false;
    log.info("=== 크롤링 작업 종료 ===");
  }

  public synchronized <T> T executeWithLock(Supplier<T> task) {
    try {
      startCrawling();
      return task.get();
    } finally {
      endCrawling();
    }
  }
}
