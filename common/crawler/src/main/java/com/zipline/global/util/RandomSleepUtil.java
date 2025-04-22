package com.zipline.global.util;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomSleepUtil {
    private static final Random random = new Random();
    
    /**
     * 지정된 범위 내에서 랜덤한 시간만큼 대기합니다.
     * @param minSeconds 최소 대기 시간(초)
     * @param maxSeconds 최대 대기 시간(초)
     */
    public static void sleep(int minSeconds, int maxSeconds) {
        try {
            int sleepTime = random.nextInt(maxSeconds - minSeconds + 1) + minSeconds;
            log.info("{}초 대기 중...", sleepTime);
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("대기 중 인터럽트 발생", e);
        }
    }
    
    /**
     * 기본 범위(2~10초)로 대기합니다.
     */
    public static void sleep() {
        sleep(2, 10);
    }
    
    /**
     * 짧은 범위(1~6초)로 대기합니다.
     */
    public static void sleepShort() {
        sleep(1, 6);
    }
} 
