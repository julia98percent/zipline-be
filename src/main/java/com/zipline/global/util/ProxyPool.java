package com.zipline.global.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.zipline.dto.publicItem.ProxyInfoDTO;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyPool {
    private final ConcurrentLinkedQueue<ProxyInfoDTO> availableProxies = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, ProxyInfoDTO> inUseProxies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> proxyFailureCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lastUsedTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> usageCount = new ConcurrentHashMap<>();
    
    @Value("${proxy.max-failures}")
    private int maxFailures;
    
    @Value("${proxy.validation-timeout}")
    private int validationTimeout;
    
    @Value("${proxy.validation-url:https}")
    private String validationUrl;
    
    @Value("${proxy.min-rotation-interval}")  // 5분
    private int minRotationInterval;
    
    @Value("${proxy.max-usage-count}")  // 최대 사용 횟수
    private int maxUsageCount;
    
    @PostConstruct
    public void init() {
        try {
            loadProxies();
            log.info("프록시 풀 초기화 완료. 총 {}개의 프록시 로드됨", availableProxies.size());
        } catch (Exception e) {
            log.error("프록시 풀 초기화 중 오류 발생: {}. 애플리케이션은 계속 실행됩니다.", e.getMessage());
        }
    }
    
    private void loadProxies() {
        try {
            ClassPathResource resource = new ClassPathResource("app/config/proxy-list.txt");
            List<String> lines = new BufferedReader(
                new InputStreamReader(resource.getInputStream())
            ).lines()
             .filter(line -> !line.trim().isEmpty())
             .collect(Collectors.toList());
            
            for (String line : lines) {
                try {
                    ProxyInfoDTO proxy = ProxyInfoDTO.fromString(line.trim());
                    availableProxies.offer(proxy);
                    proxyFailureCount.put(proxy.getKey(), new AtomicInteger(0));
                    log.debug("프록시 추가됨: {}", proxy.getKey());
                } catch (Exception e) {
                    log.warn("잘못된 프록시 형식: {}", line, e);
                }
            }
        } catch (Exception e) {
            log.error("프록시 목록 로드 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    public ProxyInfoDTO getNextAvailableProxy() {
        ProxyInfoDTO selectedProxy = null;
        int attempts = 0;
        int maxAttempts = availableProxies.size() * 2;  // 최대 시도 횟수
        
        while (selectedProxy == null && attempts < maxAttempts) {
            ProxyInfoDTO proxy = availableProxies.poll();
            if (proxy == null) {
                log.warn("사용 가능한 프록시가 없습니다. 프록시 풀을 새로고침합니다.");
                refreshProxyPool();
                proxy = availableProxies.poll();
            }
            
            if (proxy != null) {
                // 마지막 사용 시간 확인 (최소 minRotationInterval 간격)
                LocalDateTime lastUsed = lastUsedTime.get(proxy.getKey());
                if (lastUsed != null) {
                    long secondsSinceLastUse = ChronoUnit.SECONDS.between(lastUsed, LocalDateTime.now());
                    if (secondsSinceLastUse < minRotationInterval) {  // 설정된 최소 간격 사용
                        log.debug("프록시 {}는 최근에 사용되었습니다 ({}초 전, 최소 간격: {}초). 다음 프록시로 시도합니다.", 
                            proxy.getKey(), secondsSinceLastUse, minRotationInterval);
                        availableProxies.offer(proxy);
                        attempts++;
                        continue;
                    }
                }
                
                // 사용 횟수 확인
                int usage = usageCount.getOrDefault(proxy.getKey(), 0);
                if (usage >= maxUsageCount) {
                    log.debug("프록시 {}는 최대 사용 횟수를 초과했습니다 ({}회). 다음 프록시로 시도합니다.", 
                        proxy.getKey(), usage);
                    availableProxies.offer(proxy);
                    attempts++;
                    continue;
                }
                
                // 현재 사용 중인 IP 확인
                if (inUseProxies.containsValue(proxy)) {
                    log.debug("프록시 {}는 현재 다른 스레드에서 사용 중입니다. 다음 프록시로 시도합니다.", 
                        proxy.getKey());
                    availableProxies.offer(proxy);
                    attempts++;
                    continue;
                }
                
                selectedProxy = proxy;
                inUseProxies.put(proxy.getKey(), proxy);
                lastUsedTime.put(proxy.getKey(), LocalDateTime.now());
                usageCount.merge(proxy.getKey(), 1, Integer::sum);
                
                log.info("프록시 할당: {} (사용 가능: {}, 사용 중: {}, 사용 횟수: {})", 
                    proxy.getKey(), 
                    getAvailableProxyCount(), 
                    getInUseProxyCount(),
                    usageCount.get(proxy.getKey()));
            }
        }
        
        if (selectedProxy == null) {
            log.error("적절한 프록시를 찾을 수 없습니다. (사용 가능: {}, 사용 중: {})", 
                getAvailableProxyCount(), 
                getInUseProxyCount());
        }
        
        return selectedProxy;
    }
    
    public void releaseProxy(ProxyInfoDTO proxy) {
        if (proxy != null) {
            inUseProxies.remove(proxy.getKey());
            availableProxies.offer(proxy);
            log.info("프록시 반환: {} (사용 가능: {}, 사용 중: {}, 사용 횟수: {})", 
                proxy.getKey(), 
                getAvailableProxyCount(), 
                getInUseProxyCount(),
                usageCount.get(proxy.getKey()));
        }
    }
    
    public void markProxyAsFailed(ProxyInfoDTO proxy) {
        if (proxy != null) {
            AtomicInteger failures = proxyFailureCount.get(proxy.getKey());
            if (failures != null) {
                int currentFailures = failures.incrementAndGet();
                if (currentFailures >= maxFailures) {
                    log.warn("프록시 {} 실패 횟수 초과 ({}회). 풀에서 제거됨", proxy.getKey(), currentFailures);
                    proxyFailureCount.remove(proxy.getKey());
                    inUseProxies.remove(proxy.getKey());
                    lastUsedTime.remove(proxy.getKey());
                    usageCount.remove(proxy.getKey());
                } else {
                    availableProxies.offer(proxy);
                    log.info("프록시 {} 실패 ({}회). 재시도 가능 목록에 추가", proxy.getKey(), currentFailures);
                }
            }
        }
    }
    
    public int getAvailableProxyCount() {
        return availableProxies.size();
    }
    
    public int getInUseProxyCount() {
        return inUseProxies.size();
    }
    
    public List<ProxyInfoDTO> getActiveProxies() {
        return new ArrayList<>(inUseProxies.values());
    }
    
    public void refreshProxyPool() {
        log.info("프록시 풀 새로고침 시작");
        availableProxies.clear();
        inUseProxies.clear();
        proxyFailureCount.clear();
        lastUsedTime.clear();
        usageCount.clear();
        loadProxies();
        log.info("프록시 풀 새로고침 완료");
    }
} 
